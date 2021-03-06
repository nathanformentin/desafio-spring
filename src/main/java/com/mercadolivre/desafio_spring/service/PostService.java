package com.mercadolivre.desafio_spring.service;

import com.mercadolivre.desafio_spring.dto.*;
import com.mercadolivre.desafio_spring.entity.Post;
import com.mercadolivre.desafio_spring.entity.User;
import com.mercadolivre.desafio_spring.repository.IPostRepository;
import com.mercadolivre.desafio_spring.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PostService implements IPostsService {
    private static final String ASC_ORDERING = "date_asc";
    private static final String MSG_USER_NOT_FOUND = "User not found";

    private final IPostRepository postRepository;
    private final IUserRepository usersRepository;

    @Autowired
    public PostService(IPostRepository postRepository, IUserRepository userRepository) {
        this.postRepository = postRepository;
        this.usersRepository = userRepository;
    }

    @Override
    public void createPost(PostDTO postDto) {
        validateIfUserExists(postDto.getUserId());
        this.postRepository.persistPost(postDto.toEntity());
    }

    @Override
    public FollowedPostsDTO getFollowedPosts(int userId, String order) {
        User user = getUserById(userId);

        List<Post> followedPosts = fetchFollowedPostList(user);
        orderList(followedPosts, order);

        return new FollowedPostsDTO(userId, convertPostToDto(followedPosts));
    }

    @Override
    public void createPromoPost(PromoPostDTO promoPostDto) {
        validateIfUserExists(promoPostDto.getUserId());
        this.postRepository.persistPost(promoPostDto.toEntity());
    }

    @Override
    public UserPromosCountedDTO countPromoByUser(int userId) {
        User user = getUserById(userId);

        int quantityPromoPosts = this.postRepository.countPromoByUser(userId);
        return new UserPromosCountedDTO(user.getId(), user.getName(), quantityPromoPosts);
    }

    @Override
    public UserPromosDTO getPromosByUser(int userId) {
        User user = getUserById(userId);

        List<Post> listPosts = this.postRepository.fetchPromosByUser(userId);
        orderList(listPosts, "");

        List<PromoPostDTO> promoPostDTOlist = listPosts.stream()
                .map(Post::toPromoPostDTO)
                .collect(Collectors.toList());

        return new UserPromosDTO(user.getId(), user.getName(), promoPostDTOlist);
    }

    public List<PostDTO> convertPostToDto(List<Post> postList) {
        return postList.stream()
                .filter(p -> p.getDate().after(subtractTwoWeeksFromCurrentDate()))
                .map(Post::toPostDTO)
                .collect(Collectors.toList());
    }

    private void orderList(List<Post> list, String order) {
        if(Objects.equals(order, ASC_ORDERING)) {
            list.sort(Comparator.comparing(Post::getDate));
        } else {
            list.sort(Comparator.comparing(Post::getDate).reversed());
        }
    }

    private List<Post> fetchFollowedPostList(User user) {
        return user.getFollowed().stream()
        .map(postRepository::fetchPostsByUser)
        .flatMap(List::stream)
        .collect(Collectors.toList());
    }

    private Date subtractTwoWeeksFromCurrentDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.WEEK_OF_MONTH, - 2);
        return calendar.getTime();
    }

    private void validateIfUserExists(Integer userId){
       getUserById(userId);
    }

    private User getUserById(Integer userId){
       return usersRepository.fetchById(userId).orElseThrow(() -> new NoSuchElementException(MSG_USER_NOT_FOUND));
    }
}
