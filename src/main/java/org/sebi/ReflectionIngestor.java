package org.sebi;

import static dev.langchain4j.data.document.splitter.DocumentSplitters.recursive;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import io.quarkiverse.langchain4j.pgvector.PgVectorEmbeddingStore;
import io.quarkus.logging.Log;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReflectionIngestor {
    
    @Inject
    PgVectorEmbeddingStore store;

    @Inject
    EmbeddingModel embeddingModel; 

    @Inject
    ReflectionSegmentTransformer segmentTransformer;

    private EmbeddingStoreIngestor ingestor;

    public void setupIngestor(@Observes StartupEvent event){
      ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(store)
                .textSegmentTransformer(segmentTransformer)
                .embeddingModel(embeddingModel)
                .documentSplitter(recursive(500, 0))
                .build();
       
    }

    public void ingestReflection(String text) {
        if(text == null || text.isBlank()){
            return;
        }

        Log.info("Reflection : " + text);
        Document document = Document.document(text);
        ingestor.ingest(document);
        Log.info("Ingested reflection.");
    }
}
