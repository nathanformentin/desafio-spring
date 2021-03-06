package com.mercadolivre.desafio_spring.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private int id;
    private String name;
    List<Integer> followers = new ArrayList<>();
    List<Integer> followed = new ArrayList<>();
}
