package com.sunic.community.aggregate.post.store;

import com.sunic.community.aggregate.community.store.jpo.CommunityJpo;
import com.sunic.community.aggregate.community.store.repository.CommunityRepository;
import com.sunic.community.aggregate.post.store.jpo.PostJpo;
import com.sunic.community.aggregate.post.store.jpo.PostTypeJpo;
import com.sunic.community.aggregate.post.store.repository.PostRepository;
import com.sunic.community.spec.community.exception.CommunityNotFoundException;
import com.sunic.community.spec.post.entity.Post;
import com.sunic.community.spec.post.entity.PostType;
import com.sunic.community.spec.post.exception.PostNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PostStore {
    
    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    
    public Post save(Post post) {
        CommunityJpo communityJpo = communityRepository.findById(post.getCommunityId())
                .orElseThrow(() -> new CommunityNotFoundException("Community not found with id: " + post.getCommunityId()));
        
        PostJpo jpo = PostJpo.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .postType(convertToPostTypeJpo(post.getPostType()))
                .community(communityJpo)
                .registeredTime(post.getRegisteredTime())
                .registrant(post.getRegistrant())
                .modifiedTime(post.getModifiedTime())
                .modifier(post.getModifier())
                .build();
        
        PostJpo saved = postRepository.save(jpo);
        return convertToPost(saved);
    }
    
    public Post findById(Integer id) {
        PostJpo jpo = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + id));
        return convertToPost(jpo);
    }
    
    public List<Post> findByCommunityId(Integer communityId) {
        return postRepository.findByCommunityIdOrderByRegisteredTimeDesc(communityId).stream()
                .map(this::convertToPost)
                .collect(Collectors.toList());
    }
    
    public Post update(Post post) {
        PostJpo jpo = postRepository.findById(post.getId())
                .orElseThrow(() -> new PostNotFoundException("Post not found with id: " + post.getId()));
        
        jpo.updateContent(
                post.getTitle(),
                post.getContent(),
                convertToPostTypeJpo(post.getPostType()),
                post.getModifier()
        );
        
        PostJpo saved = postRepository.save(jpo);
        return convertToPost(saved);
    }
    
    public void deleteById(Integer id) {
        if (!postRepository.existsById(id)) {
            throw new PostNotFoundException("Post not found with id: " + id);
        }
        postRepository.deleteById(id);
    }
    
    private Post convertToPost(PostJpo jpo) {
        return Post.builder()
                .id(jpo.getId())
                .title(jpo.getTitle())
                .content(jpo.getContent())
                .postType(convertToPostType(jpo.getPostType()))
                .communityId(jpo.getCommunity().getId())
                .registeredTime(jpo.getRegisteredTime())
                .registrant(jpo.getRegistrant())
                .modifiedTime(jpo.getModifiedTime())
                .modifier(jpo.getModifier())
                .build();
    }
    
    private PostType convertToPostType(PostTypeJpo typeJpo) {
        if (typeJpo == null) return null;
        return PostType.valueOf(typeJpo.name());
    }
    
    private PostTypeJpo convertToPostTypeJpo(PostType type) {
        if (type == null) return null;
        return PostTypeJpo.valueOf(type.name());
    }
}