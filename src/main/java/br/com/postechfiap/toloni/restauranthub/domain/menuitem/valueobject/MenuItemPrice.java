package br.com.postechfiap.toloni.restauranthub.domain.menuitem.valueobject;

import br.com.postechfiap.toloni.restauranthub.domain.shared.exception.DomainException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/// Represents the price of a [MenuItem].
///
/// This is an immutable value object that wraps a [java.math.BigDecimal]
/// and a [java.util.Currency], ensuring the price is never null, never
/// negative, and always stored with the correct scale for the given currency.
///
/// ## Example
/// ```java
/// var price = MenuItemPrice.of(new BigDecimal("12.90"), Currency.getInstance("BRL"));
/// var price = MenuItemPrice.ofBRL(new BigDecimal("12.90"));
/// ```
public final class MenuItemPrice {

    private final BigDecimal amount;
    private final Currency currency;

    private MenuItemPrice(BigDecimal amount, Currency currency) {
        if (amount == null)
            throw new DomainException("MenuItem price is required.");
        if (currency == null)
            throw new DomainException("MenuItem currency is required.");
        if (amount.compareTo(BigDecimal.ZERO) < 0)
            throw new DomainException("MenuItem price must not be negative.");
        this.currency = currency;
        this.amount = amount.setScale(currency.getDefaultFractionDigits(), RoundingMode.HALF_UP);
    }

    /// Creates a `MenuItemPrice` from a [java.math.BigDecimal] and a [java.util.Currency].
    ///
    /// The amount is automatically scaled to the number of fraction digits
    /// defined by the currency (e.g. 2 for BRL, 0 for JPY).
    ///
    /// @param amount   the price amount
    /// @param currency the currency of the price
    /// @return a new `MenuItemPrice`
    /// @throws DomainException if `amount` is null, negative, or `currency` is null
    public static MenuItemPrice of(BigDecimal amount, Currency currency) {
        return new MenuItemPrice(amount, currency);
    }

    /// Creates a `MenuItemPrice` in Brazilian Real (BRL).
    ///
    /// @param amount the price amount
    /// @return a new `MenuItemPrice` with currency set to BRL
    /// @throws DomainException if `amount` is null or negative
    public static MenuItemPrice ofBRL(BigDecimal amount) {
        return new MenuItemPrice(amount, Currency.getInstance("BRL"));
    }

    /// Creates a `MenuItemPrice` in US Dollar (USD).
    ///
    /// @param amount the price amount
    /// @return a new `MenuItemPrice` with currency set to USD
    /// @throws DomainException if `amount` is null or negative
    public static MenuItemPrice ofUSD(BigDecimal amount) {
        return new MenuItemPrice(amount, Currency.getInstance("USD"));
    }

    /// @return the price amount, scaled to the currency's fraction digits
    public BigDecimal getAmount() {
        return amount;
    }

    /// @return the [java.util.Currency] of this price
    public Currency getCurrency() {
        return currency;
    }

    /// @return the currency symbol followed by the amount (e.g. `R$ 12,90`)
    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount.toPlainString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItemPrice other)) return false;
        return amount.compareTo(other.amount) == 0
                && Objects.equals(currency, other.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }
}