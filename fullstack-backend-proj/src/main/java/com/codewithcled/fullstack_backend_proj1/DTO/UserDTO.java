package com.codewithcled.fullstack_backend_proj1.DTO;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private Integer elo;

    public UserDTO(Long id,String username,String email,String role,Integer elo){
        this.id=id;
        this.username=username;
        this.email=email;
        this.role=role;
        this.elo=elo;
    }

    public Long getId(){
        return id;
    }

    public String getUsername(){
        return username;
    }

    public Integer getElo() {
        return elo;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}


