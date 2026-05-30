package com.offline_upi_mesh.domain;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Entity
@Data
@NoArgsConstructor
public class VirtualNode {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String nodeName;


    private boolean internetAvailable;


    @ElementCollection
    @Column(length = 5000)
    private List<String> storedPackets =
            new ArrayList<>();

}