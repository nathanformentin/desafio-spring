package com.mercadolivre.desafio_spring.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowedPostsDTO  {
    private int userId;
    private List<PostDTO> posts = new ArrayList<>();
}
