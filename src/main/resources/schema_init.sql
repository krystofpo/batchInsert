
    drop table Book if exists


    drop table Loan if exists


    drop table User if exists


    drop sequence if exists id_seq
create sequence id_seq start with 1 increment by 50


    create table Book (
       id bigint not null,
        name varchar(255),
        loan_id bigint,
        primary key (id)
    )

    create table Loan (
       id bigint not null,
        name varchar(255),
        user_id bigint,
        primary key (id)
    )


    create table User (
       id bigint not null,
        hasLoans boolean not null,
        name varchar(255),
        primary key (id)
    )


    alter table Book
       add constraint "FK_BOOK_LOAN"
       foreign key (loan_id)
       references Loan


    alter table Loan
       add constraint "FK_LOAN_USER"
       foreign key (user_id)
       references User