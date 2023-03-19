package go.kb.searchserver.domain;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@NoArgsConstructor
@ToString(of = {"id", "keyword", "readCount"})
@Table(name = "keyword", indexes = @Index(name = "idx_count", columnList = "read_count DESC"))
public class Keyword {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String keyword;

    @Column(name = "read_count")
    private int readCount;

    public Keyword(String keyword, int readCount) {
        this.keyword = keyword;
        this.readCount = readCount;
    }

    public void increment() {
        readCount++;
    }
}
