package bg.sofia.uni.fmi.mjt.virtualwallet.core;

import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.Card;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.GoldenCard;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.card.StandardCard;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.payment.PaymentInfo;
import bg.sofia.uni.fmi.mjt.virtualwallet.core.transaction.Transaction;

import java.time.LocalDateTime;


public class VirtualWallet implements VirtualWalletAPI {
    private static final int MAX_WALLET_SIZE = 5;
    private static final int MAX_TRANSACTIONS = 10;
    private int cardCounter;
    private Card[] cards;
    private Transaction[] transactions;
    private int transactionIndex;

    public VirtualWallet() {
        cards = new Card[MAX_WALLET_SIZE];
        transactions = new Transaction[MAX_TRANSACTIONS];
    }

    public boolean registerCard(Card card) {
        if(cardCounter >= MAX_WALLET_SIZE || card == null || card.getName() == null) {
            return false;
        }

        for(int i = 0; i < cardCounter; ++i) {
            if(card.getName().equals(cards[i].getName())) {
                return false;
            }
        }
        cards[cardCounter++] = card;
        return true;
    }

    public boolean executePayment(Card card, PaymentInfo paymentInfo) {
        if(paymentInfo == null || card == null) {
            return false;
        }
        if (card.executePayment(paymentInfo.getCost())) {
            addTransaction(card, LocalDateTime.now(), paymentInfo);
            return true;
        }
        return false;
    }

    public boolean feed(Card card, double amount) {
        if (card == null || this.getCardByName(card.getName()) == null) {
            return false;
        }

        if(card.setAmount(card.getAmount() + amount)) {
            return true;
        }
        return false;
    }

    public Card getCardByName(String name) {
        for(int i = 0; i < cardCounter; ++i) {
            if(name.equals(cards[i].getName())) {
                return cards[i];
            }
        }
        return null;
    }

    public int getTotalNumberOfCards() {
        return cardCounter;
    }

    protected boolean addTransaction(Card card, LocalDateTime date, PaymentInfo paymentInfo) {
        if (card == null || paymentInfo == null) {
            return false;
        }
        if (transactionIndex == MAX_TRANSACTIONS) {
            transactionIndex = 0;
        }

        Transaction newTransaction = new Transaction(card.getName(), date, paymentInfo);

        transactions[transactionIndex++] = newTransaction;
        return true;
    }
}
