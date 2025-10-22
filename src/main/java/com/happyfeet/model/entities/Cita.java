package com.happyfeet.model.entities;

import com.happyfeet.model.entities.enums.CitaEstado;

import java.time.LocalDateTime;
import java.util.Objects;

public class Cita {
    private Long id;
    private Long idVeterinario;
    private Long idMascota; // placeholder para futura relación
    private LocalDateTime inicio;
    private LocalDateTime fin;
    private CitaEstado estado = CitaEstado.PROGRAMADA;
    private String motivo;

    public Cita() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(Long idVeterinario) { this.idVeterinario = idVeterinario; }

    public Long getIdMascota() { return idMascota; }
    public void setIdMascota(Long idMascota) { this.idMascota = idMascota; }

    public LocalDateTime getInicio() { return inicio; }
    public void setInicio(LocalDateTime inicio) { this.inicio = inicio; }

    public LocalDateTime getFin() { return fin; }
    public void setFin(LocalDateTime fin) { this.fin = fin; }

    public CitaEstado getEstado() { return estado; }
    public void setEstado(CitaEstado estado) { this.estado = Objects.requireNonNull(estado); }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}
