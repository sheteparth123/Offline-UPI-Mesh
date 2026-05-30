package com.offline_upi_mesh.repository;


import com.offline_upi_mesh.domain.VirtualNode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface VirtualNodeRepository
        extends JpaRepository<VirtualNode, Long> {


    Optional<VirtualNode> findByNodeName(
            String nodeName
    );

}