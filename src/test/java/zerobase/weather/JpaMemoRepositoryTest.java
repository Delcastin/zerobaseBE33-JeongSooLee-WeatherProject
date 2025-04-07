package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional  // 테스트를 진행하면서 데이터베이스에 생기는 변경사항들을 적용하지 않게 해준다.
public class JpaMemoRepositoryTest {

    @Autowired
    private JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        // given
        Memo newMemo = new Memo();
        newMemo.setText("This is a new jpa memo");

        // when
        jpaMemoRepository.save(newMemo);
        int savedMemoId = newMemo.getId();

        // then
        Optional<Memo> resultOptional = jpaMemoRepository.findById(newMemo.getId());

        Memo result = resultOptional.orElseThrow(() -> new NoSuchElementException("Memo not found"));

        assertNotNull(result);  // 객체가 존재하는지 확인
        assertEquals("This is a new jpa memo", result.getText());  // 저장된 텍스트가 맞는지 확인
    }


}

