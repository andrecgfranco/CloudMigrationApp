package juliana.auth_forum_service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 150, message = "Title must have between 3 and 150 characters")
    private String title;

    @NotBlank(message = "Content is required")
    @Size(min = 5, max = 5000, message = "Content must have between 5 and 5000 characters")
    private String content;

    @NotNull(message = "Author Id is required")
    private Long authorId;

    public CreatePostRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }
}