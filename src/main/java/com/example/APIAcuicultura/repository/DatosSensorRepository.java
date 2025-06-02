package com.example.APIAcuicultura.repository;

import com.example.APIAcuicultura.entity.DatosSensor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DatosSensorRepository extends JpaRepository<DatosSensor, Long> {
    // Método para obtener todos los DatosSensor para un estanque específico
    List<DatosSensor> findBySensorEstanqueIdOrderByTimestampAsc(Long estanqueId);

    List<DatosSensor> findBySensorIdAndSensorEstanqueIdOrderByTimestampAsc(Long sensorId, Long estanqueId);

    List<DatosSensor> findBySensorIdOrderByTimestampAsc(Long sensorId);
}
