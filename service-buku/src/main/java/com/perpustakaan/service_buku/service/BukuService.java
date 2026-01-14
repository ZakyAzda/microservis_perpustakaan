package com.perpustakaan.service_buku.service;

import com.perpustakaan.service_buku.dto.BukuRequest;
import com.perpustakaan.service_buku.entity.Buku;
import com.perpustakaan.service_buku.repository.BukuRepository;
import com.perpustakaan.service_buku.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import java.util.Map;

@Service
public class BukuService {

    @Autowired
    private BukuRepository bukuRepository;

    public Buku saveBuku(BukuRequest request) {
        Buku buku = new Buku();
        buku.setKodeBuku(request.getKodeBuku());
        buku.setJudul(request.getJudul());
        buku.setPengarang(request.getPengarang());
        buku.setPenerbit(request.getPenerbit());
        buku.setTahunTerbit(request.getTahunTerbit());
        return bukuRepository.save(buku);
    }

    public Buku getBukuById(Long id) {
        return bukuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Buku dengan ID " + id + " tidak ditemukan!"));
    }

    public List<Buku> getAllBuku() {
        return bukuRepository.findAll();
    }

    public Buku updateBuku(Long id, BukuRequest request) {
        Buku buku = bukuRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gagal Update: Buku dengan ID " + id + " tidak ditemukan!"));

        buku.setKodeBuku(request.getKodeBuku());
        buku.setJudul(request.getJudul());
        buku.setPengarang(request.getPengarang());
        buku.setPenerbit(request.getPenerbit());
        buku.setTahunTerbit(request.getTahunTerbit());

        return bukuRepository.save(buku);
    }

    public void deleteBuku(Long id) {
        if (!bukuRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gagal Hapus: Buku dengan ID " + id + " tidak ditemukan!");
        }
        bukuRepository.deleteById(id);
    }

    @RabbitListener(queues = "stok-buku-queue")
    public void prosesUpdateStok(Map<String, Object> event) {
        System.out.println("Menerima Event dari RabbitMQ: " + event);
        
        try {
            Long bukuId = Long.valueOf(event.get("bukuId").toString());
            String action = (String) event.get("action");

            if ("PINJAM".equals(action)) {
                Buku buku = bukuRepository.findById(bukuId).orElse(null);
                if (buku != null) {
                    int stokSekarang = buku.getStok();
                    if (stokSekarang > 0) {
                        buku.setStok(stokSekarang - 1);
                        bukuRepository.save(buku);
                        System.out.println("Sukses kurangi stok Buku ID: " + bukuId + ". Sisa stok: " + (stokSekarang - 1));
                    } else {
                        System.out.println("Gagal kurangi stok: Stok Buku ID " + bukuId + " sudah habis!");
                    }
                } else {
                    System.out.println("Buku dengan ID " + bukuId + " tidak ditemukan.");
                }
            }
        } catch (Exception e) {
            System.err.println("Error memproses pesan RabbitMQ: " + e.getMessage());
        }
    }
}
