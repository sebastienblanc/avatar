package org.sebi;

import java.util.Date;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.segment.TextSegmentTransformer;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Default;

@ApplicationScoped
@Default
public class ReflectionSegmentTransformer implements TextSegmentTransformer{

    @ConfigProperty(name = "avatar.name")
    String avatarName;

    private Metadata metaData;

    public Metadata getMetaData() {
        return metaData;
    }

    public void setMetaData(Metadata metaData) {
        this.metaData = metaData;
    }

    @Override
    public TextSegment transform(TextSegment segment) {
       Metadata metaData = segment.metadata();
       metaData.put("creationDate", new Date().toString());
       metaData.put("avatarName", avatarName);
       return TextSegment.from(segment.text(), metaData);
    }
    
}
