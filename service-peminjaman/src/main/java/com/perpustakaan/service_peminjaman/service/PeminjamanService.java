package com.perpustakaan.service_peminjaman.service;

import com.perpustakaan.service_peminjaman.config.RabbitMQConfig;
import com.perpustakaan.service_peminjaman.dto.PeminjamanRequest;
import com.perpustakaan.service_peminjaman.entity.Peminjaman;
import com.perpustakaan.service_peminjaman.exception.ResourceNotFoundException;
import com.perpustakaan.service_peminjaman.repository.PeminjamanRepository;
import com.perpustakaan.service_peminjaman.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import java.util.Map;
import java.util.HashMap;

@Service
public class PeminjamanService {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Peminjaman savePeminjaman(PeminjamanRequest request) {
        Peminjaman peminjaman = new Peminjaman();
        peminjaman.setAnggotaId(request.getAnggotaId());
        peminjaman.setBukuId(request.getBukuId());
        peminjaman.setTanggalPinjam(request.getTanggalPinjam());
        peminjaman.setTanggalKembali(request.getTanggalKembali());
        peminjaman.setStatus(request.getStatus());
        
        Peminjaman savedPeminjaman = peminjamanRepository.save(peminjaman);

        // 2. LOGIKA CQRS: Kirim Event ke RabbitMQ
        try {
            Map<String, Object> event = new HashMap<>();
            event.put("bukuId", request.getBukuId());
            event.put("action", "PINJAM");
            
            System.out.println("Mengirim event ke RabbitMQ: " + event);
            
            rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE, 
                RabbitMQConfig.ROUTING_KEY, 
                event
            );
        } catch (Exception e) {
            System.err.println("Gagal mengirim event RabbitMQ: " + e.getMessage());
        }

        return savedPeminjaman;
    }

    public ResponseTemplateVO getPeminjamanWithDetails(Long peminjamanId) {
        ResponseTemplateVO vo = new ResponseTemplateVO();
        Peminjaman peminjaman = peminjamanRepository.findById(peminjamanId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Data Peminjaman dengan ID " + peminjamanId + " tidak ditemukan!"));

        vo.setPeminjaman(peminjaman);

        try {
            Anggota anggota = restTemplate
                    .getForObject("http://localhost:8081/api/anggota/" + peminjaman.getAnggotaId(), Anggota.class);
            vo.setAnggota(anggota);

            String bukuUrl = "http://localhost:8082"; // Assumed port for Buku Service
            Buku buku = restTemplate.getForObject(bukuUrl + "/api/buku/" + peminjaman.getBukuId(), Buku.class);
            vo.setBuku(buku);
        } catch (Exception e) {
            // Jika service tidak tersedia, tetap return data peminjaman
        }

        return vo;
    }

    public Peminjaman updatePeminjaman(Long id, PeminjamanRequest request) {
        Peminjaman peminjaman = peminjamanRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gagal Update: Data Peminjaman ID " + id + " tidak ditemukan!"));

        peminjaman.setAnggotaId(request.getAnggotaId());
        peminjaman.setBukuId(request.getBukuId());
        peminjaman.setTanggalPinjam(request.getTanggalPinjam());
        peminjaman.setTanggalKembali(request.getTanggalKembali());
        peminjaman.setStatus(request.getStatus());

        return peminjamanRepository.save(peminjaman);
    }

    public void deletePeminjaman(Long id) {
        if (!peminjamanRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gagal Hapus: Data Peminjaman ID " + id + " tidak ditemukan!");
        }
        
        peminjamanRepository.deleteById(id);
    }
}