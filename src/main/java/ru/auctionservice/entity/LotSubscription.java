package ru.auctionservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lot_subscriptions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "lot_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "lot_id", nullable = false)
    private Long lotId;
}
