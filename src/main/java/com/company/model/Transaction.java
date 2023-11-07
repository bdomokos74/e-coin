package com.company.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

import java.io.Serializable;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;

import static com.company.util.KeyHelper.getPrivateKey;
import static com.company.util.KeyHelper.getPublicKey;

@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
@Entity(name = "TRANSACTIONS")
public class Transaction implements Serializable {
   @Id
   @GeneratedValue
   private UUID id;

   @Column(name = "FROM_W")
   private byte[] from;
   @Column(name = "TO_W")
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
      Base64.Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(from);
   }

   public String getToFX() {
      Base64.Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(to);
   }
   public String getSignatureFX() {
      Base64.Encoder encoder = Base64.getEncoder();
      return encoder.encodeToString(this.signature) ;
   }
}
