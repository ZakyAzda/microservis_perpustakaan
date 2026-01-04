package com.perpustakaan.service_peminjaman.service;

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

@Service
public class PeminjamanService {

    @Autowired
    private PeminjamanRepository peminjamanRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private DiscoveryClient discoveryClient;

    public Peminjaman savePeminjaman(PeminjamanRequest request) {
        Peminjaman peminjaman = new Peminjaman();
        peminjaman.setAnggotaId(request.getAnggotaId());
        peminjaman.setBukuId(request.getBukuId());
        peminjaman.setTanggalPinjam(request.getTanggalPinjam());
        peminjaman.setTanggalKembali(request.getTanggalKembali());
        peminjaman.setStatus(request.getStatus());
        return peminjamanRepository.save(peminjaman);
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