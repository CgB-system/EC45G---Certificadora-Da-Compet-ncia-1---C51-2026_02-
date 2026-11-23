package br.edu.tedi.administrador.repository;

import br.edu.tedi.administrador.entity.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {
}
