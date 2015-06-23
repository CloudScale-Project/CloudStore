SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `tpcw` ;
CREATE SCHEMA IF NOT EXISTS `tpcw` DEFAULT CHARACTER SET utf8 COLLATE utf8_slovenian_ci ;
USE `tpcw` ;

-- -----------------------------------------------------
-- Table `tpcw`.`author`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`author` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`author` (
  `A_ID` INT NOT NULL AUTO_INCREMENT ,
  `A_FNAME` VARCHAR(20) NULL ,
  `A_LNAME` VARCHAR(20) NULL ,
  `A_MNAME` VARCHAR(20) NULL ,
  `A_DOB` DATETIME NULL ,
  `A_BIO` TEXT NULL ,
  PRIMARY KEY (`A_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`item`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`item` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`item` (
  `I_ID` INT NOT NULL AUTO_INCREMENT ,
  `I_TITLE` VARCHAR(60) NULL ,
  `I_A_ID` INT NOT NULL ,
  `I_PUB_DATE` DATETIME NULL ,
  `I_PUBLISHER` VARCHAR(60) NULL ,
  `I_SUBJECT` VARCHAR(60) NULL ,
  `I_DESC` TEXT NULL ,
  `I_RELATED1` INT NULL ,
  `I_RELATED2` INT NULL ,
  `I_RELATED3` INT NULL ,
  `I_RELATED4` INT NULL ,
  `I_RELATED5` INT NULL ,
  `I_THUMBNAIL` VARCHAR(60) NULL ,
  `I_IMAGE` VARCHAR(60) NULL ,
  `I_SRP` INT NULL ,
  `I_COST` INT NULL ,
  `I_AVAIL` DATETIME NULL ,
  `I_STOCK` INT NULL ,
  `I_ISBN` VARCHAR(13) NULL ,
  `I_PAGE` VARCHAR(4) NULL ,
  `I_BACKING` VARCHAR(15) NULL ,
  `I_DIMENSION` VARCHAR(25) NULL ,
  INDEX `fk_ITEM_AUTHOR` (`I_A_ID` ASC) ,
  PRIMARY KEY (`I_ID`) ,
  CONSTRAINT `fk_ITEM_AUTHOR`
    FOREIGN KEY (`I_A_ID` )
    REFERENCES `tpcw`.`author` (`A_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`country`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`country` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`country` (
  `CO_ID` INT NOT NULL AUTO_INCREMENT ,
  `CO_NAME` VARCHAR(50) NULL ,
  `CO_EXCHANGE` DOUBLE NULL ,
  `CO_CURRENCY` VARCHAR(18) NULL ,
  PRIMARY KEY (`CO_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`address`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`address` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`address` (
  `ADDR_ID` INT NOT NULL AUTO_INCREMENT ,
  `ADDR_STREET1` VARCHAR(40) NULL ,
  `ADDR_STREET2` VARCHAR(40) NULL ,
  `ADDR_CITY` VARCHAR(30) NULL ,
  `ADDR_STATE` VARCHAR(20) NULL ,
  `ADDR_ZIP` VARCHAR(10) NULL ,
  `ADDR_CO_ID` INT NOT NULL ,
  PRIMARY KEY (`ADDR_ID`) ,
  INDEX `fk_ADDRESS_COUNTRY1` (`ADDR_CO_ID` ASC) ,
  CONSTRAINT `fk_ADDRESS_COUNTRY1`
    FOREIGN KEY (`ADDR_CO_ID` )
    REFERENCES `tpcw`.`country` (`CO_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`customer`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`customer` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`customer` (
  `C_ID` INT NOT NULL AUTO_INCREMENT ,
  `C_UNAME` VARCHAR(20) NULL ,
  `C_PASSWD` VARCHAR(20) NULL ,
  `C_FNAME` VARCHAR(15) NULL ,
  `C_LNAME` VARCHAR(15) NULL ,
  `C_ADDR_ID` INT NOT NULL ,
  `C_PHONE` VARCHAR(16) NULL ,
  `C_EMAIL` VARCHAR(50) NULL ,
  `C_SINCE` DATE NULL ,
  `C_LAST_VISIT` DATE NULL ,
  `C_LOGIN` DATETIME NULL ,
  `C_EXPIRATION` DATETIME NULL COMMENT '	' ,
  `C_DISCOUNT` INT NULL ,
  `C_BALANCE` INT NULL ,
  `C_YTD_PMT` INT NULL ,
  `C_BIRTHDATE` DATE NULL ,
  `C_DATA` TEXT NULL ,
  PRIMARY KEY (`C_ID`) ,
  INDEX `fk_CUSTOMER_ADDRESS1` (`C_ADDR_ID` ASC) ,
  CONSTRAINT `fk_CUSTOMER_ADDRESS1`
    FOREIGN KEY (`C_ADDR_ID` )
    REFERENCES `tpcw`.`address` (`ADDR_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`orders`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`orders` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`orders` (
  `O_ID` INT NOT NULL AUTO_INCREMENT ,
  `O_C_ID` INT NOT NULL ,
  `O_DATE` DATETIME NULL ,
  `O_SUB_TOTAL` INT NULL ,
  `O_TAX` INT NULL ,
  `O_TOTAL` INT NULL ,
  `O_SHIP_TYPE` VARCHAR(10) NULL ,
  `O_SHIP_DATE` DATETIME NULL ,
  `O_BILL_ADDR_ID` INT NOT NULL ,
  `O_SHIP_ADDR_ID` INT NOT NULL ,
  `O_STATUS` VARCHAR(15) NULL ,
  PRIMARY KEY (`O_ID`) ,
  INDEX `fk_ORDERS_CUSTOMER1` (`O_C_ID` ASC) ,
  INDEX `fk_ORDERS_ADDRESS1` (`O_BILL_ADDR_ID` ASC) ,
  INDEX `fk_ORDERS_ADDRESS2` (`O_SHIP_ADDR_ID` ASC) ,
  CONSTRAINT `fk_ORDERS_CUSTOMER1`
    FOREIGN KEY (`O_C_ID` )
    REFERENCES `tpcw`.`customer` (`C_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDERS_ADDRESS1`
    FOREIGN KEY (`O_BILL_ADDR_ID` )
    REFERENCES `tpcw`.`address` (`ADDR_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDERS_ADDRESS2`
    FOREIGN KEY (`O_SHIP_ADDR_ID` )
    REFERENCES `tpcw`.`address` (`ADDR_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`order_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`order_line` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`order_line` (
  `OL_ID` INT NOT NULL ,
  `OL_O_ID` INT NOT NULL ,
  `OL_I_ID` INT NOT NULL ,
  `OL_QTY` INT NULL ,
  `OL_DISCOUNT` INT NULL ,
  `OL_COMMENT` VARCHAR(100) NULL ,
  PRIMARY KEY (`OL_ID`) ,
  INDEX `fk_ORDER_LINE_ITEM1` (`OL_I_ID` ASC) ,
  INDEX `fk_ORDER_LINE_ORDERS1` (`OL_O_ID` ASC) ,
  CONSTRAINT `fk_ORDER_LINE_ITEM1`
    FOREIGN KEY (`OL_I_ID` )
    REFERENCES `tpcw`.`item` (`I_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_ORDER_LINE_ORDERS1`
    FOREIGN KEY (`OL_O_ID` )
    REFERENCES `tpcw`.`orders` (`O_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`cc_xacts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`cc_xacts` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`cc_xacts` (
  `CX_O_ID` INT NOT NULL AUTO_INCREMENT ,
  `CX_TYPE` VARCHAR(10) NULL ,
  `CX_NUM` INT NULL ,
  `CX_NAME` VARCHAR(31) NULL ,
  `CX_EXPIRY` DATE NULL ,
  `CX_AUTH_ID` VARCHAR(15) NULL ,
  `CX_XACT_AMT` DECIMAL(15,2) NULL ,
  `CX_XACT_DATE` DATETIME NULL ,
  `CX_CO_ID` INT NOT NULL ,
  INDEX `fk_CC_XACTS_ORDERS` (`CX_O_ID` ASC) ,
  INDEX `fk_CC_XACTS_COUNTRY` (`CX_CO_ID` ASC) ,
  CONSTRAINT `fk_CC_XACTS_ORDERS`
    FOREIGN KEY (`CX_O_ID` )
    REFERENCES `tpcw`.`orders` (`O_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_CC_XACTS_COUNTRY`
    FOREIGN KEY (`CX_CO_ID` )
    REFERENCES `tpcw`.`country` (`CO_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`shopping_cart`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`shopping_cart` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`shopping_cart` (
  `SC_ID` INT NOT NULL AUTO_INCREMENT ,
  `SC_TIME` TIMESTAMP NULL ,
  PRIMARY KEY (`SC_ID`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tpcw`.`shopping_cart_line`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `tpcw`.`shopping_cart_line` ;

CREATE  TABLE IF NOT EXISTS `tpcw`.`shopping_cart_line` (
  `SCL_SC_ID` INT NOT NULL AUTO_INCREMENT ,
  `SCL_QTY` INT NULL ,
  `SCL_I_ID` INT NOT NULL ,
  PRIMARY KEY (`SCL_SC_ID`) ,
  INDEX `fk_shopping_cart_line_item1` (`SCL_I_ID` ASC) ,
  CONSTRAINT `fk_shopping_cart_line_item1`
    FOREIGN KEY (`SCL_I_ID` )
    REFERENCES `tpcw`.`item` (`I_ID` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
