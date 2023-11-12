package com.company.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.util.DigestUtils;

import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static com.company.util.KeyHelper.getPublicKey;

@Entity
@Table(name = "TRANSACTIONS")
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

   //Constructor for loading with existing signature
   /*
   public Transaction(byte[] from, byte[] to, Integer value, byte[] signature, Integer ledgerId, String timeStamp) {
      this.from = from;
//      this.fromFX = encoder.encodeToString(from);
      this.to = to;
//      this.toFX = encoder.encodeToString(to);
      this.value = value;
      this.signature = signature;
//      this.signatureFX = encoder.encodeToString(signature);
      this.ledgerId = ledgerId;
      this.timestamp = timeStamp;
   }
   */


   //Constructor for creating a new transaction and signing it.
   /*
   public Transaction (Wallet fromWallet,
                       byte[] toAddress,
                       Integer value,
                       Integer ledgerId,
                       Signature signing) throws InvalidKeyException, SignatureException {
      this.from = getPublicKey(fromWallet).getEncoded();
//      this.fromFX = encoder.encodeToString(getPublicKey(fromWallet).getEncoded());
      this.to = toAddress;
//      this.toFX = encoder.encodeToString(toAddress);
      this.value = value;
      this.ledgerId = ledgerId;
      this.timestamp = LocalDateTime.now().toString();
      signing.initSign(getPrivateKey(fromWallet));
      String sr = this.toString();
      signing.update(sr.getBytes());
      this.signature = signing.sign();
//      this.signatureFX = encoder.encodeToString(this.signature);
   }
*/

   public Boolean isVerified(Signature signing)
           throws InvalidKeyException, SignatureException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {

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
//      Base64.Encoder encoder = Base64.getEncoder();
//      return encoder.encodeToString(from);
      return DigestUtils.md5DigestAsHex(from);
   }

   public String getToFX() {
//      Base64.Encoder encoder = Base64.getEncoder();
//      return encoder.encodeToString(to);
      return DigestUtils.md5DigestAsHex(to);
   }
   public String getSignatureFX() {
//      Base64.Encoder encoder = Base64.getEncoder();
//      return encoder.encodeToString(this.signature) ;
      return DigestUtils.md5DigestAsHex(this.signature);
   }
}
