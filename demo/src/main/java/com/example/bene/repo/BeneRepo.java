package com.example.bene.repo;

import com.example.bene.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BeneRepo {

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
    static final int INT_DEL_FLAG=6;
    static final int INT_CREATED_DATE=7;
    static final int INT_STATUS=8;
    static final int INT_MIGRATION_STATUS=9;

    static final int INT_BENE_ID=1;
    static final int INT_ACCOUNT_NAME=2;
    static final int INT_ACCOUNT_NUMBER=3;
    static final int INT_IFSC=4;
    static final int INT_AMOUNT=5;
    static final int INT_BANK=6;
    static final int INT_BRANCH=7;
    static final int INT_DEL_ACCT_FLAG=8;
    static final int INT_ACCOUNT_TYPE=9;

    static final int GET_BENE_BY_NICK_NAME=1;

    private static final Logger log = LoggerFactory.getLogger(BeneRepo.class);

    String insertbene="Insert into bene(bene_name,bene_nick_name,mobile,email,referenceId,delFlag,createdDate,status,migration_status) values(?,?,?,?,?,?,?,?,?)";
    String insteraccount="Insert into account(bene_id,account_name,account_number,ifsc,amount,bank,branch,delAccFlag,accountType) values(?,?,?,?,?,?,?,?,?)";

    String findone="Select * from bene where bene_nick_name=?";
    String accQuery = "SELECT * FROM account WHERE bene_id = ?";

    String beneupdate = "Update bene Set bene_name=?,mobile=?,email=?,lastupdated=?,status=? where bene_nick_name=?";
    String accupdate="Update account Set account_name=?,ifsc=?,amount=?,lastupdated=?,accountType=? where bene_id=?";



    public String insert(Bene bene) throws SQLException {

        Connection con= DriverManager.getConnection(url,userName,password);
        con.setAutoCommit(false);


       try(PreparedStatement ps = con.prepareStatement(insertbene,Statement.RETURN_GENERATED_KEYS)){
           ps.setString(INT_BENE_NAME,bene.getBeneName());
           ps.setString(INT_BENE_NICK_NAME,bene.getBeneNicknName());
           ps.setString(INT_MOBILE,bene.getMobile());
           ps.setString(INT_EMAIL,bene.getEmail());
           ps.setString(INT_REFERENCE_ID,bene.getReferenceId());
           ps.setString(INT_DEL_FLAG,bene.getDelFlag());
           ps.setDate(INT_CREATED_DATE,bene.getCreatedDate());
           ps.setString(INT_STATUS,bene.getStatus());
           ps.setString(INT_MIGRATION_STATUS,bene.getMigrationStatus());
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
               as.setString(INT_DEL_ACCT_FLAG,ac.getDeleAcctFlag());
               as.setString(INT_ACCOUNT_TYPE,ac.getAccountType());
               int affectedrows=  as.executeUpdate();

               log.info("account affected rows",affectedrows);
           }
           con.commit();
           log.info("Beneficiary successfully created");
           return "Success";
           }
       catch(Exception e)
       {
           con.rollback();
           log.warn("exception occured",e);
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
               PreparedStatement ps1 = con.prepareStatement(accQuery)){
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
                   bene.setDelFlag(rs.getString("delFlag"));
                   bene.setCreatedDate(rs.getDate("createdDate"));
                   bene.setStatus(rs.getString("status"));
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
                        account.setDeleAcctFlag(rs2.getString("delAccFlag"));
                        account.setAccountType(rs2.getString("accountType"));
                        account.setBeneId(beneId);
                        accounts.add(account);
                   }
                   bene.setAccount(accounts);
               }
           }
         return bene;
       }

    public List list(ListRequest request) throws SQLException {
       List<Bene>beneList=new ArrayList<>();
       if(request.isFetchChild()){
           Connection con = DriverManager.getConnection(url, userName, password);

           int pg=0;
           int size=0;
           String selectAll="";
           if(request.getSort()!=null && request.getPage()!=null){
               int page =request.getPage().get(0).getPage();
               size=request.getPage().get(0).getSize();
               pg=page*size;
               String  value =request.getSort().get(0).getSortBy();
               String sortOrder=request.getSort().get(0).getSort();
            selectAll = "SELECT * FROM bene ORDER BY " + value + " " + sortOrder + " LIMIT ?, ?";
           }
           else{
               String name = request.getFilters().get(0).getName();
               String filedValue= request.getFilters().get(0).getValue();
               selectAll = "SELECT * FROM bene WHERE " + name + " = '" + filedValue + "'";
           }


           try (PreparedStatement ps = con.prepareStatement(selectAll)){
               if(request.getSort()!=null && request.getPage()!=null) {
                   ps.setInt(1, pg);
                   ps.setInt(2, size);
               }
               ResultSet rs = ps.executeQuery();
               while (rs.next()){
                   String beneNickname=rs.getString("bene_nick_name");
                   Bene bene=findone(beneNickname);
                   if(bene.getDelFlag()!=null) {
                       if (!bene.getDelFlag().equalsIgnoreCase("Y")) {
                           beneList.add(bene);
                       }
                   }else{
                   beneList.add(bene);
                   }
               }
           }
       }
       return beneList;
    }

   public String Delete(DeleteRequest request) throws SQLException {
       Connection con = DriverManager.getConnection(url, userName, password);
       String delFlag=request.getDelFlag();
       String remarks= request.getRemarks();
       String  bene_nick_name= request.getBeneNickName();

       Bene bene=findone( bene_nick_name);
       Long bene_id= bene.getBeneId();
       List<Account>acc=bene.getAccount();
       String delAccFlag="Y";
       String update ="Update Bene Set delFlag=?,remarks=? where  bene_nick_name=?";
       String accupdate="Update account Set delAccFlag=? where bene_id=?";
        try(PreparedStatement ps = con.prepareStatement(update);
            PreparedStatement ps1= con.prepareStatement(accupdate))
        {
          ps.setString(1,delFlag);
          ps.setString(2,remarks);
          ps.setString(3, bene_nick_name);
          ps.executeUpdate();
          for (Account ac : acc){
              ps1.setString(1,delAccFlag);
              ps1.setLong(2, bene_id);
              ps1.executeUpdate();
          }

        } catch(Exception e)
        {
            con.rollback();
            log.warn("exception occured",e);
            return "Can't able to delete";
        }
        log.info("Beneficiary deleted");
       return "Beneficiary Successfully marked as deleted";
   }

       public String amend(Amend request) throws SQLException {

        Connection con= DriverManager.getConnection(url,userName,password);
        con.setAutoCommit(false);

        try(PreparedStatement ps = con.prepareStatement(beneupdate)){
            ps.setString(1,request.getBeneName());
            ps.setString(2,request.getMobile());
            ps.setString(3,request.getEmail());
            ps.setDate(4, request.getLastupdated());
            ps.setString(5,request.getStatus());
            ps.setString(6,request.getBeneNicknName());
            ps.executeUpdate();
            Bene bn = findone(request.getBeneNicknName());

            if(request.getAccount()!=null) {
                for (Account ac : request.getAccount()) {
                    PreparedStatement as = con.prepareStatement(accupdate);
                    as.setString(1, ac.getAccountName());
                    as.setString(2, ac.getIFSC());
                    as.setDouble(3, ac.getAmount());
                    as.setDate(4, (Date) ac.getLastupdated());
                    as.setString(5, ac.getAccountType());
                    as.setLong(6, bn.getBeneId());
                    int affectedrows = as.executeUpdate();
                }
            }
            con.commit();

            log.info("bene amended successfully");
            return "Updated Successfully";
        }
        catch(Exception e)
        {
            con.rollback();
            log.warn("exception=>",e);
            return "Give the Fields correctly";
        }
        finally {
            con.close();
        }
    }
}


