package com.gamenawa.gamenawabatch.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Getter
@Setter
public class BaseEntity {
    @CreatedDate
    private LocalDateTime modifiedDt;

    @LastModifiedDate
    private LocalDateTime createdDt;
}
