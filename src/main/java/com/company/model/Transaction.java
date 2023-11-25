package com.company.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.UUID;

import static com.company.util.KeyHelper.getPublicKey;

@Entity
@Table(name = "TRANSACTIONS", schema = "ecoin")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public class Transaction implements Serializable {
   @Id
   @GeneratedValue
   @JdbcTypeCode(SqlTypes.CHAR)
   @Column(name = "ID", nullable = false, length = 36)
   private UUID id;

   @Column(name = "FROM_W", length = 2048 )
   private byte[] from;
   @Column(name = "TO_W", length = 2048 )
   private byte[] to;
   @Column(name = "VALUE_V")
   private Integer value;
   @Column(name = "CREATED_ON")
   private String timestamp;
   @Column(name = "SIGNATURE")
   @EqualsAndHashCode.Include
   private byte[] signature;
   @Column(name = "LEDGER_ID")
   private Long ledgerId;

   public Transaction(byte[] from, byte[] to, Integer value, String timestamp, byte[] signature, Long ledgerId) {
      this.from = from;
      this.to = to;
      this.value = value;
      this.timestamp = timestamp;
      this.signature = signature;
      this.ledgerId = ledgerId;
   }

   public Boolean isVerified(Signature signing) throws InvalidKeyException, SignatureException {
      PublicKey publicKey = getPublicKey(this.getFrom());
      signing.initVerify(publicKey);
      signing.update(this.toString().getBytes());
      return signing.verify(this.signature);
   }

   @Override
   public String toString() {
      return "Transaction{" +
              "from=" + Arrays.toString(from) +
              ", to=" + Arrays.toString(to) +
              ", value=" + value +
              ", timeStamp= " + timestamp +
              ", ledgerId=" + ledgerId +
              '}';
   }

   public String getFromFX() {
      return DigestUtils.md5DigestAsHex(from);
   }

   public String getToFX() {
      return DigestUtils.md5DigestAsHex(to);
   }
   public String getSignatureFX() {
      return DigestUtils.md5DigestAsHex(this.signature);
   }
}
