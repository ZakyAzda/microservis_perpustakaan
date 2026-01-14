# 📚 Sistem Microservices Perpustakaan

<div align="center">

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.5-brightgreen?style=flat&logo=spring-boot)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?style=flat&logo=docker)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Messaging-orange?style=flat&logo=rabbitmq)
![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-red?style=flat&logo=jenkins)

**Tugas Besar Pemrograman Terdistribusi / Arsitektur Microservices**
<br>
**Nama:** [Ahmad Zaky Azda]
<br>
**NIM:** [2311082003]

</div>

---

## 📝 Deskripsi Proyek

Project ini adalah implementasi sistem manajemen perpustakaan menggunakan arsitektur **Microservices**. Sistem ini menerapkan komunikasi antar service menggunakan **RabbitMQ** (Event-Driven), dipantau menggunakan **ELK Stack**, dan dideploy menggunakan pipeline otomasisasi **Jenkins**.

Project ini dibuat untuk memenuhi tugas yang mencakup:
1.  Implementasi Microservices (Spring Boot)
2.  Monitoring System (ELK Stack)
3.  CI/CD Pipeline (Jenkins)

---

## 📂 Struktur & Lokasi Konfigurasi

Sesuai instruksi tugas, berikut adalah lokasi file konfigurasi penting dalam repository ini:

| Komponen | Lokasi File Konfigurasi | Keterangan |
|----------|-------------------------|------------|
| **Docker Orchestration** | `./docker-compose.yml` | Mengatur semua container (App, DB, RabbitMQ, ELK) |
| **Jenkins Pipeline** | `./Jenkinsfile` | Langkah-langkah build & deploy otomatis |
| **Logstash Pipeline** | `./monitoring/logstash/pipeline/logstash.conf` | Konfigurasi parsing log service |
| **Kibana Dashboard** | `./monitoring/kibana/kibana.yml` | Konfigurasi visualisasi monitoring |
| **RabbitMQ Config** | `src/main/resources/application.properties` | Ada di setiap folder service masing-masing |

---

## 🛠️ Daftar Service & Port

Berikut adalah daftar service yang berjalan dalam sistem ini:

| Service | Port | Fungsi Utama |
|---------|------|--------------|
| **Eureka Server** | `8761` | Service Discovery (Pusat pendaftaran service) |
| **API Gateway** | `8080` | Pintu masuk utama (Routing) |
| **Service Anggota** | `8081` | Manajemen data anggota |
| **Service Buku** | `8082` | Manajemen data buku |
| **Service Peminjaman** | `8083` | Transaksi peminjaman |
| **Service Pengembalian**| `8084` | Transaksi pengembalian & denda |
| **RabbitMQ UI** | `15672`| Monitoring pesan/event queue |
| **Jenkins** | `9000` | Automation Server |
| **Kibana** | `5601` | Dashboard Monitoring Logs |

---

## 🚀 Langkah Instalasi & Menjalankan (Run)

Pastikan **Docker** dan **Docker Compose** sudah terinstall di komputer Anda.

### 1. Clone & Persiapan
```bash
git clone [https://github.com/ZakyAzda/microservis_perpustakaan.git](https://github.com/ZakyAzda/microservis_perpustakaan.git)
