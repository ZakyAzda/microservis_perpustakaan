package com.perpustakaan.service_pengembalian.service;

import com.perpustakaan.service_pengembalian.dto.PengembalianRequest;
import com.perpustakaan.service_pengembalian.entity.Pengembalian;
import com.perpustakaan.service_pengembalian.repository.PengembalianRepository;
import com.perpustakaan.service_pengembalian.vo.Peminjaman;
import com.perpustakaan.service_pengembalian.vo.ResponseTemplateVO;
import com.perpustakaan.service_pengembalian.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Service
public class PengembalianService {

    @Autowired
    private PengembalianRepository pengembalianRepository;

    @Autowired
    private RestTemplate restTemplate;

    public Pengembalian savePengembalian(PengembalianRequest request) {
        Pengembalian pengembalian = new Pengembalian();
        pengembalian.setPeminjamanId(request.getPeminjamanId());
        pengembalian.setTanggalDikembalikan(request.getTanggalDikembalikan());
        pengembalian.setTerlambat(request.getTerlambat());
        pengembalian.setDenda(request.getDenda());
        return pengembalianRepository.save(pengembalian);
    }

    public ResponseTemplateVO getPengembalianWithDetails(Long id) {
        Pengembalian pengembalian = pengembalianRepository.findById(id)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Data Pengembalian dengan ID " + id + " tidak ditemukan!"));

        ResponseTemplateVO vo = new ResponseTemplateVO();
        vo.setPengembalian(pengembalian);

        try {
            // Ambil data Peminjaman dari Service Peminjaman (localhost:8083)
            String peminjamanUrl = "http://localhost:8083";
            Peminjaman peminjaman = restTemplate.getForObject(
                    peminjamanUrl + "/api/peminjaman/" + pengembalian.getPeminjamanId(),
                    Peminjaman.class);
            vo.setPeminjaman(peminjaman);
        } catch (Exception e) {
            // Jika service tidak tersedia, tetap return data pengembalian
        }

        return vo;
    }

    public List<Pengembalian> getAllPengembalian() {
        return pengembalianRepository.findAll();
    }

    public Pengembalian updatePengembalian(Long id, PengembalianRequest request) {
        Pengembalian pengembalian = pengembalianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gagal Update: Data Pengembalian ID " + id + " tidak ditemukan!"));

        pengembalian.setPeminjamanId(request.getPeminjamanId());
        pengembalian.setTanggalDikembalikan(request.getTanggalDikembalikan());
        pengembalian.setTerlambat(request.getTerlambat());
        pengembalian.setDenda(request.getDenda());

        return pengembalianRepository.save(pengembalian);
    }

    public void deletePengembalian(Long id) {
        if (!pengembalianRepository.existsById(id)) {
            throw new ResourceNotFoundException("Gagal Hapus: Data Pengembalian ID " + id + " tidak ditemukan!");
        }
        pengembalianRepository.deleteById(id);
    }
}