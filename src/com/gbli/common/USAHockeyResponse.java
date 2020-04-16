package com.gbli.common;

public class USAHockeyResponse {
    public String status;
    public String message;
    public USAHockeyData data;

    public USAHockeyResponse() {

    }

    @Override
    public String toString() {
        return "USAHockeyResponse{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data.toString() +
                '}';
    }

    public class USAHockeyData  {
        public String confirmation_number;
        public String last_name;
        public String first_name;
        public String middle_initial;
        public String address_1;
        public String address_2;
        public String city;
        public String state;
        public String zip;
        public String dob;
        public String country;
        public String for_zip;
        public String citizenship;
        public String gender;
        public String h_phone;
        public String b_phone;
        public String PGSL_name;
        public String PGSF_name;
        public String PGSM_name;
        public String email;

        public USAHockeyData () {}

        @Override
        public String toString() {
            return "USAHockeyData{" +
                    "confirmation_number='" + confirmation_number + '\'' +
                    ", last_name='" + last_name + '\'' +
                    ", first_name='" + first_name + '\'' +
                    ", middle_initial='" + middle_initial + '\'' +
                    ", address_1='" + address_1 + '\'' +
                    ", address_2='" + address_2 + '\'' +
                    ", city='" + city + '\'' +
                    ", state='" + state + '\'' +
                    ", zip='" + zip + '\'' +
                    ", dob='" + dob + '\'' +
                    ", country='" + country + '\'' +
                    ", for_zip='" + for_zip + '\'' +
                    ", citizenship='" + citizenship + '\'' +
                    ", gender='" + gender + '\'' +
                    ", h_phone='" + h_phone + '\'' +
                    ", b_phone='" + b_phone + '\'' +
                    ", PGSL_name='" + PGSL_name + '\'' +
                    ", PGSF_name='" + PGSF_name + '\'' +
                    ", PGSM_name='" + PGSM_name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
