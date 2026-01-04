package com.perpustakaan.service_peminjaman.controller;

import com.perpustakaan.service_peminjaman.dto.PeminjamanRequest;
import com.perpustakaan.service_peminjaman.entity.Peminjaman;
import com.perpustakaan.service_peminjaman.service.PeminjamanService;
import com.perpustakaan.service_peminjaman.vo.ResponseTemplateVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/peminjaman")
public class PeminjamanController {

    @Autowired
    private PeminjamanService peminjamanService;

    @PostMapping
    public ResponseEntity<Peminjaman> savePeminjaman(@RequestBody PeminjamanRequest request) {
        Peminjaman saved = peminjamanService.savePeminjaman(request);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // @GetMapping
    // public List<Peminjaman> getPeminjaman() {
    // return peminjamanService.getAllPeminjaman();
    // }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseTemplateVO> getPeminjaman(@PathVariable("id") Long id) {
        ResponseTemplateVO response = peminjamanService.getPeminjamanWithDetails(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public Peminjaman updatePeminjaman(@PathVariable("id") Long id, @RequestBody PeminjamanRequest request) {
        return peminjamanService.updatePeminjaman(id, request);
    }

    @DeleteMapping("/{id}")
    public void deletePeminjaman(@PathVariable("id") Long id) {
        peminjamanService.deletePeminjaman(id);
    }
}