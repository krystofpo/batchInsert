CREATE TABLE Book
(
  id      BIGINT AUTO_INCREMENT NOT NULL,
  loan_id BIGINT                NULL,
  CONSTRAINT BookPK PRIMARY KEY (id)
) engine = InnoDB;

CREATE TABLE Loan
(
  id      BIGINT AUTO_INCREMENT NOT NULL,
  user_id BIGINT                NULL,
  CONSTRAINT LoanPK PRIMARY KEY (id)
) engine = InnoDB;


CREATE TABLE User
(
  id       BIGINT AUTO_INCREMENT NOT NULL,
  hasLoans BIT(1)                NOT NULL,
  name     VARCHAR(255)          NULL,
  CONSTRAINT UserPK PRIMARY KEY (id)
) engine = InnoDB;

ALTER TABLE Book
  ADD CONSTRAINT
    FK_84hs0u3lu0gstf3ax1rgpc6xe FOREIGN KEY (loan_id) REFERENCES Loan (id);


ALTER TABLE Loan
  ADD CONSTRAINT
    FK_8iyyajvtwyscdd2ni257rgwxv FOREIGN KEY (user_id) REFERENCES User (id);
