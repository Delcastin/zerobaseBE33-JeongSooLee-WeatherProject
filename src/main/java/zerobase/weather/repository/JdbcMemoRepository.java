package zerobase.weather.repository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;


    @Autowired
    public JdbcMemoRepository(DataSource dataSource){
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo){
        String sql = "insert into memo values(?,?,?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText(), memo.getVersion());
        return memo;
    }

    public List<Memo> findAll(){
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper());
    }

    public Optional<Memo> findById(int id){
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }

    private RowMapper<Memo> memoRowMapper(){
        //Resultset
        // {id = 1, text = 'this is memo~'}
        return(rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text"),
                rs.getInt("version")
        );
    }

    public int update(Memo memo){
        // version 값을 확인하고 업데이트
        String sql = "update memo set text = ?, version = version + 1 where id = ? and version = ?";
        int rowsAffected = jdbcTemplate.update(sql, memo.getText(), memo.getId(), memo.getVersion());

        if (rowsAffected == 0) {
            // version이 일치하지 않으면 업데이트되지 않음
            throw new OptimisticLockingFailureException("Memo was modified by another transaction.");
        }
        return rowsAffected;
    }
}
