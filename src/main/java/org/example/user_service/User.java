package org.example.user_service;

import java.time.LocalDateTime;

public class User {
        private Long id;

        private String name;

        private String email;

        private Integer age;

        private LocalDateTime createdat;

        public  User() {
            
        }

        public User(String name, String email, Integer age) {
            this.name = name;
            this.email = email;
            this.age = age;
            this.createdat = LocalDateTime.now();
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public LocalDateTime getCreatedat() {
            return createdat;
        }

        public void setCreatedat(LocalDateTime createdat) {
            this.createdat = createdat;
        }

}
