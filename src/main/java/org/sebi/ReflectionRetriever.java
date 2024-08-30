package org.sebi;


import java.util.function.Supplier;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import io.quarkiverse.langchain4j.pgvector.PgVectorEmbeddingStore;

import static dev.langchain4j.store.embedding.filter.MetadataFilterBuilder.metadataKey;


@ApplicationScoped
public class ReflectionRetriever implements Supplier<RetrievalAugmentor> {

    private EmbeddingStoreContentRetriever retriever;

    private EmbeddingModel model;

    private PgVectorEmbeddingStore store;

    @ConfigProperty(name = "avatar.name")
    String avatarName;

    ReflectionRetriever(PgVectorEmbeddingStore store, EmbeddingModel model) {
        this.store = store;
        this.model = model;
    }

    @Override
    public RetrievalAugmentor get() {
        retriever = EmbeddingStoreContentRetriever.builder()
        .embeddingModel(model)
        .embeddingStore(store)
        .filter(metadataKey("avatarName").isEqualTo(avatarName))
        .maxResults(10)
        .build();
        return DefaultRetrievalAugmentor.builder()
                .contentRetriever(retriever)
                .build();
    }
}
