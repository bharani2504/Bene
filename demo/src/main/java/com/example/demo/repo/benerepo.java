package com.example.demo.repo;

import com.example.demo.model.Account;
import com.example.demo.model.Bene;
import com.example.demo.model.ListResponse;
import org.hibernate.sql.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class benerepo {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${spring.datasource.url}")
    String url;
    @Value("${spring.datasource.username}")
    String userName;
    @Value("${spring.datasource.password}")
    String password;


    static final int INT_BENE_NAME=1;
    static final int INT_BENE_NICK_NAME=2;
    static final int INT_MOBILE=3;
    static final int INT_EMAIL=4;
    static final int INT_REFERENCE_ID=5;


    static final int INT_BENE_ID=1;
    static final int INT_ACCOUNT_NAME=2;
    static final int INT_ACCOUNT_NUMBER=3;
    static final int INT_IFSC=4;
    static final int INT_AMOUNT=5;
    static final int INT_BANK=6;
    static final int INT_BRANCH=7;

    static final int GET_BENE_BY_NICK_NAME=1;

    String insertbene="Insert into bene(bene_name,bene_nick_name,mobile,email) values(?,?,?,?)";
    String insteraccount="Insert into account(bene_id,account_name,account_number,ifsc,amount,bank,branch) values(?,?,?,?,?,?,?)";

    String findone="Select * from bene where bene_nick_name=?";
    String accQuery = "SELECT * FROM account WHERE bene_id = ?";


    String delete= "Delete from bene where bene_nick_name=?";
    String accDelete="Delete from account where bene_id=?";


    String SelectAll="Select * from bene";

    public String insert(Bene bene) throws SQLException {

        Connection con= DriverManager.getConnection(url,userName,password);
        con.setAutoCommit(false);

       try(PreparedStatement ps = con.prepareStatement(insertbene,Statement.RETURN_GENERATED_KEYS)){
           ps.setString(INT_BENE_NAME,bene.getBeneName());
           ps.setString(INT_BENE_NICK_NAME,bene.getBeneNicknName());
           ps.setString(INT_MOBILE,bene.getMobile());
           ps.setString(INT_EMAIL,bene.getEmail());
           ps.setString(INT_REFERENCE_ID,bene.getReferenceId());
           ps.executeUpdate();
           ResultSet re = ps.getGeneratedKeys();
           re.next();
           int total = re.getInt(INT_BENE_ID);

           for( Account ac : bene.getAccount()){
               PreparedStatement as = con.prepareStatement(insteraccount);
               as.setLong(INT_BENE_ID,total);
               as.setString(INT_ACCOUNT_NAME,ac.getAccountName());
               as.setString(INT_ACCOUNT_NUMBER,ac.getAccountNumber());
               as.setString(INT_IFSC,ac.getIFSC());
               as.setString(INT_BANK,ac.getBank());
               as.setDouble(INT_AMOUNT,ac.getAmount());
               as.setString(INT_BRANCH,ac.getBranch());
               int afffectedrows=  as.executeUpdate();
           }
           con.commit();
           return "Success";
           }
       catch(Exception e)
       {
           con.rollback();
           return "Give the Fields correctly";
       }
       finally {
           con.close();
       }
       }

       public Bene findone(String beneNicknName) throws SQLException {
           Connection con= DriverManager.getConnection(url,userName,password);
           Bene bene=new Bene();
           List<Account>accounts=new ArrayList<>();
           try(PreparedStatement ps = con.prepareStatement(findone);
               PreparedStatement ps1 = con.prepareStatement(accQuery) ){
               ps.setString(GET_BENE_BY_NICK_NAME,beneNicknName);
               ResultSet rs =ps.executeQuery();

               if (rs.next()){
                   long beneId=rs.getLong("bene_id");
                   bene.setBeneId(beneId);
                   bene.setBeneName(rs.getString("bene_name"));
                   bene.setBeneNicknName(rs.getString("bene_nick_name"));
                   bene.setMobile(rs.getString("mobile"));
                   bene.setEmail(rs.getString("email"));
                   bene.setReferenceId(rs.getString("referenceId"));
                   ps1.setLong(1,beneId);
                    ResultSet rs2=ps1.executeQuery();
                    while (rs2.next()){
                        Account account=new Account();
                        account.setAccountName(rs2.getString("account_name"));
                        account.setAccountNumber(rs2.getString("account_number"));
                        account.setIFSC(rs2.getString("ifsc"));
                        account.setAmount(rs2.getDouble("amount"));
                        account.setBank(rs2.getString("bank"));
                        account.setId(rs2.getLong("account_id"));
                        account.setBranch(rs2.getString("branch"));
                        account.setBeneId(beneId);
                        accounts.add(account);
                   }
                   bene.setAccount(accounts);
               }
           }
         return bene;
       }

    public String delete(String beneNicknName) throws SQLException {
        Connection con = DriverManager.getConnection(url, userName, password);

        try (PreparedStatement ps = con.prepareStatement(delete);
             PreparedStatement ps1 = con.prepareStatement(accDelete)) {
            Bene bene =findone(beneNicknName);
            ps1.setLong(INT_BENE_ID,bene.getBeneId());
            ps1.executeUpdate();
            ps.setString(INT_BENE_NAME,beneNicknName);
            ps.executeUpdate();
            con.close();
        }
        catch(Exception e)
        {
         throw  new SQLException();
        }

        return "successfully deleted";
    }

    public List list(boolean fetchChild) throws SQLException {
       List<Bene>beneList=new ArrayList<>();
       if(fetchChild){
           Connection con = DriverManager.getConnection(url, userName, password);
           try (PreparedStatement ps = con.prepareStatement(SelectAll)){
               ResultSet rs = ps.executeQuery();
               while (rs.next()){
                   String beneNickname=rs.getString("bene_nick_name");
                   beneList.add(findone(beneNickname));
               }
           }
       }
       return beneList;
    }
}

