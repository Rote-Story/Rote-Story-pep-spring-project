package com.example.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;

@Service
public class AccountService {

    AccountRepository accountRepository;

    /*
     * Constructor dependency injection for accountRepository
     */
    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Saves account object to database
     * 
     * @param account
     * @return Account object saved to database
     */
    public Account persistAccount(Account account) {
        return accountRepository.save(account);
    }

    /**
     * Fetches an account from the database with a matching username.
     * 
     * @param username - an account username
     * @return account retrieved from database if it exists, null otherwise
     */
    public Account getAccountByUsername(String username) {
        Optional<Account> optionalAccount = accountRepository.findByUsername(username);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account;
        } else {
            return null;
        }
    }

    /**
     * Fetches an account from the database with a matching password.
     * 
     * @param password - an account password
     * @return account retrieved from database if it exists, null otherwise
     */
    public Account getAccountByPassword(String password) {
        Optional<Account> optionalAccount = accountRepository.findByPassword(password);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account;
        } else {
            return null;
        }
    }

    /**
     * Fetches an account with a matching username and password.
     * 
     * @param account - an account object with a username and password
     * @return a matching account if one is found, null otherwise
     */
    public Account getAccountByLoginCredentials(Account account) {
        String password = account.getPassword();
        String username = account.getUsername();

        Optional<Account> optionalAccount = accountRepository.findByUsernameAndPassword(username, password);
        if (optionalAccount.isPresent()) {
            account = optionalAccount.get();
            return account;
        } else {
            return null;
        }
    }

    /**
     * Fetches an account matching the id parameter.
     * 
     * @param id - the account id used to search the database
     * @return an account object matching the id parameter
     */
    public Account getAccountById(Integer id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return account;
        } else {
            return null;
        }
    }

}
