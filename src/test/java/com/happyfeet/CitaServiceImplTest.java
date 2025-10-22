package com.happyfeet;

import com.happyfeet.model.entities.Cita;
import com.happyfeet.model.entities.enums.CitaEstado;
import com.happyfeet.repository.CitaRepository;
import com.happyfeet.service.CitaService;
import com.happyfeet.service.impl.CitaServiceImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class CitaServiceImplTest {

    static class InMemoryCitaRepository implements CitaRepository {
        private final Map<Long, Cita> data = new HashMap<>();
        private long seq = 1L;

        @Override
        public synchronized Cita save(Cita cita) {
            if (cita.getId() == null) cita.setId(seq++);
            data.put(cita.getId(), cloneCita(cita));
            return cloneCita(cita);
        }

        @Override
        public synchronized Cita update(Cita cita) {
            Objects.requireNonNull(cita.getId(), "id requerido");
            data.put(cita.getId(), cloneCita(cita));
            return cloneCita(cita);
        }

        @Override
        public synchronized Optional<Cita> findById(Long id) {
            Cita c = data.get(id);
            return Optional.ofNullable(c == null ? null : cloneCita(c));
        }

        @Override
        public synchronized void deleteById(Long id) {
            data.remove(id);
        }

        @Override
        public synchronized List<Cita> findByVeterinarioAndRange(Long idVet, LocalDateTime desde, LocalDateTime hasta) {
            List<Cita> out = new ArrayList<>();
            for (Cita c : data.values()) {
                if (Objects.equals(c.getIdVeterinario(), idVet)) {
                    LocalDateTime ini = c.getInicio();
                    if ((ini.isEqual(desde) || ini.isAfter(desde)) && (ini.isEqual(hasta) || ini.isBefore(hasta))) {
                        out.add(cloneCita(c));
                    }
                }
            }
            out.sort(Comparator.comparing(Cita::getInicio));
            return out;
        }

        @Override
        public synchronized List<Cita> findByDate(LocalDate fecha) {
            List<Cita> out = new ArrayList<>();
            for (Cita c : data.values()) {
                if (c.getInicio() != null && c.getInicio().toLocalDate().isEqual(fecha)) {
                    out.add(cloneCita(c));
                }
            }
            out.sort(Comparator.comparing(Cita::getInicio));
            return out;
        }

        @Override
        public synchronized List<Cita> findByEstado(CitaEstado estado) {
            List<Cita> out = new ArrayList<>();
            for (Cita c : data.values()) {
                if (estado.equals(c.getEstado())) out.add(cloneCita(c));
            }
            out.sort(Comparator.comparing(Cita::getInicio));
            return out;
        }

        @Override
        public synchronized boolean existsOverlap(Long idVet, LocalDateTime inicio, LocalDateTime fin) {
            for (Cita c : data.values()) {
                if (!Objects.equals(c.getIdVeterinario(), idVet)) continue;
                LocalDateTime ini = c.getInicio();
                if ((ini.isEqual(inicio) || ini.isAfter(inicio)) && (ini.isEqual(fin) || ini.isBefore(fin))) {
                    return true;
                }
            }
            return false;
        }

        private static Cita cloneCita(Cita c) {
            Cita x = new Cita();
            x.setId(c.getId());
            x.setIdVeterinario(c.getIdVeterinario());
            x.setIdMascota(c.getIdMascota());
            x.setInicio(c.getInicio());
            x.setFin(c.getFin());
            x.setEstado(c.getEstado());
            x.setMotivo(c.getMotivo());
            return x;
        }
    }

    @Test
    void crearYFlujoDeEstados_basico() {
        CitaService service = new CitaServiceImpl(new InMemoryCitaRepository());
        LocalDateTime t = LocalDateTime.now().plusHours(1);

        Cita c = new Cita();
        c.setIdVeterinario(1L);
        c.setIdMascota(1L);
        c.setInicio(t);
        c.setMotivo("Consulta general");
        Cita creada = service.crear(c);
        assertNotNull(creada.getId());
        assertEquals(CitaEstado.PROGRAMADA, creada.getEstado());

        service.confirmar(creada.getId());
        assertEquals(CitaEstado.CONFIRMADA, service.buscarPorId(creada.getId()).get().getEstado());

        service.iniciar(creada.getId());
        assertEquals(CitaEstado.EN_CURSO, service.buscarPorId(creada.getId()).get().getEstado());

        service.finalizar(creada.getId());
        assertEquals(CitaEstado.FINALIZADA, service.buscarPorId(creada.getId()).get().getEstado());

        // Cancelar luego de finalizada debe fallar
        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.cancelar(creada.getId()));
        assertNotNull(ex.getMessage());
    }

    @Test
    void solapeHorarios() {
        CitaService service = new CitaServiceImpl(new InMemoryCitaRepository());
        LocalDateTime t1 = LocalDateTime.now().plusHours(2);

        Cita c1 = new Cita();
        c1.setIdVeterinario(10L);
        c1.setIdMascota(5L);
        c1.setInicio(t1);
        c1.setMotivo("Control");
        service.crear(c1);

        assertTrue(service.haySolape(10L, t1.minusMinutes(5), t1.plusMinutes(5)));
        assertFalse(service.haySolape(10L, t1.plusMinutes(31), t1.plusMinutes(60)));
    }

    @Test
    void listarPorFechaYEstado() {
        InMemoryCitaRepository repo = new InMemoryCitaRepository();
        CitaService service = new CitaServiceImpl(repo);

        LocalDateTime base = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0);
        for (int i = 0; i < 3; i++) {
            Cita c = new Cita();
            c.setIdVeterinario(1L);
            c.setIdMascota((long) (i + 1));
            c.setInicio(base.plusMinutes(i * 30));
            c.setMotivo("Consulta " + i);
            repo.save(c);
        }

        assertEquals(3, service.listarPorFecha(base.toLocalDate()).size());
        assertEquals(3, service.listarPorEstado(CitaEstado.PROGRAMADA).size());
    }
}
