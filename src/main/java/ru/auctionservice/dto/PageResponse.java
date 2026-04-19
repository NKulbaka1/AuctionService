package ru.auctionservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Schema(description = "Paginated response wrapper")
public class PageResponse<T> {

    @Schema(description = "List of items on the current page")
    private final List<T> content;

    @Schema(description = "Current page number (0-based)", example = "0")
    private final int page;

    @Schema(description = "Page size", example = "10")
    private final int size;

    @Schema(description = "Total number of elements", example = "42")
    private final long totalElements;

    @Schema(description = "Total number of pages", example = "5")
    private final int totalPages;

    @Schema(description = "Whether this is the first page", example = "true")
    private final boolean first;

    @Schema(description = "Whether this is the last page", example = "false")
    private final boolean last;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.first = page.isFirst();
        this.last = page.isLast();
    }
}
