package com.inventorysystem.Backend.mapper;

import com.inventorysystem.Backend.dto.article.ArticleDTO;
import com.inventorysystem.Backend.model.Article;
import com.inventorysystem.Backend.model.Category;
import com.inventorysystem.Backend.model.Provider;
import com.inventorysystem.Backend.repository.CategoryRepository;
import com.inventorysystem.Backend.repository.ProviderRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ArticleMapper {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ProviderRepository providerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProviderMapper providerMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * Converts an Article entity to an ArticleDTO.
     *
     * @param article the Article entity
     * @return the ArticleDTO representation
     */
    public ArticleDTO articleToDTO(Article article) {
        if (article == null) {
            return null; // Handle null article input gracefully
        }

        // Map basic fields from Article to ArticleDTO using ModelMapper
        ArticleDTO convertedArticle = modelMapper.map(article, ArticleDTO.class);

        // Fetch and set Provider details
        Provider foundProvider = providerRepository.getProviderById(article.getProviderId());
        if (foundProvider != null) {
            convertedArticle.setProvider(providerMapper.providerToDTO(foundProvider));
        } else {
            convertedArticle.setProvider(null); // Handle case where Provider is not found
        }

        // Fetch and set Category details
        Category foundCategory = categoryRepository.getCategoryById(article.getCategoryId());
        if (foundCategory != null) {
            convertedArticle.setCategory(categoryMapper.categoryToDTO(foundCategory));
        } else {
            convertedArticle.setCategory(null); // Handle case where Category is not found
        }

        return convertedArticle;
    }
}
