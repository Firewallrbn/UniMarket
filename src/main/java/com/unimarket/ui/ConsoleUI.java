package com.unimarket.ui;

import com.unimarket.auth.IAuthService;
import com.unimarket.market.IMarketService;
import com.unimarket.market.MarketService;
import com.unimarket.model.Product;
import com.unimarket.model.IPhysicalProduct;
import com.unimarket.model.IDigitalProduct;
import com.unimarket.model.User;

import java.util.List;
import java.util.Scanner;

import static com.unimarket.ui.ColorUtils.*;

/**
 * Interfaz de usuario por consola. Orquesta los flujos de login y mercado.
 */
public class ConsoleUI {

    private final IAuthService authService;
    private final IMarketService marketService;
    private final Scanner scanner;
    private User currentUser;

    public ConsoleUI(IAuthService authService, IMarketService marketService) {
        this.authService = authService;
        this.marketService = marketService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * Punto de entrada principal de la UI.
     */
    public void start() {
        showBanner();
        showLoginMenu();
        showMarketMenu();
        System.out.println("\n" + exito("¡Gracias por usar UniMarket! Hasta pronto."));
        scanner.close();
    }

    // ==================== LOGIN ====================

    private void showBanner() {
        System.out.println(CIAN + NEGRITA);
        System.out.println("  ╔════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("  ║                                                                                ║");
        System.out.println("  ║   ██╗   ██╗███╗   ██╗██╗███╗   ███╗ █████╗ ██████╗ ██╗  ██╗███████╗████████╗  ║");
        System.out.println("  ║   ██║   ██║████╗  ██║██║████╗ ████║██╔══██╗██╔══██╗██║ ██╔╝██╔════╝╚══██╔══╝  ║");
        System.out.println("  ║   ██║   ██║██╔██╗ ██║██║██╔████╔██║███████║██████╔╝█████╔╝ █████╗     ██║     ║");
        System.out.println("  ║   ██║   ██║██║╚██╗██║██║██║╚██╔╝██║██╔══██║██╔══██╗██╔═██╗ ██╔══╝     ██║     ║");
        System.out.println("  ║   ╚██████╔╝██║ ╚████║██║██║ ╚═╝ ██║██║  ██║██║  ██║██║  ██╗███████╗   ██║     ║");
        System.out.println("  ║    ╚═════╝ ╚═╝  ╚═══╝╚═╝╚═╝     ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚══════╝   ╚═╝     ║");
        System.out.println("  ║                                                                                ║");
        System.out.println("  ║" + AMARILLO + "                    Marketplace Universitario v1.0 " + CIAN
                + "                           ║");
        System.out.println("  ║" + DIM + "                      Compra y vende entre estudiantes" + RESET
                + CIAN + NEGRITA + "                        ║");
        System.out.println("  ║                                                                                ║");
        System.out.println("  ╚════════════════════════════════════════════════════════════════════════════════╝");
        System.out.println(RESET);
    }

    private void showLoginMenu() {
        while (currentUser == null) {
            System.out.println(separador());
            System.out.println(titulo("  🔐 INICIO DE SESIÓN"));
            System.out.println(separador());
            System.out.print(CIAN + "  Ingrese su usuario: " + RESET);

            String username = scanner.nextLine().trim();

            if (username.isEmpty()) {
                System.out.println(error("El nombre de usuario no puede estar vacío."));
                continue;
            }

            currentUser = authService.login(username);

            if (currentUser == null) {
                System.out.println(error("Usuario '" + username + "' no encontrado. Intente de nuevo."));
            } else {
                System.out.println();
                System.out.println(exito("¡Bienvenido/a, " + NEGRITA + currentUser.getUsername() + RESET
                        + VERDE + "!"));
                System.out.println(info("Tipo de cuenta: " + NEGRITA
                        + currentUser.getType().getDisplayName()));
                System.out.println();
            }
        }
    }

    // ==================== MERCADO ====================

    private void showMarketMenu() {
        boolean running = true;

        while (running) {
            System.out.println(separador());
            System.out.println(titulo("  🛒 MENÚ PRINCIPAL"));
            System.out.println(separador());
            System.out.println(AMARILLO + "  [1]" + RESET + " 📦 Ver Catálogo");
            System.out.println(AMARILLO + "  [2]" + RESET + " ➕ Agregar al Carrito");
            System.out.println(AMARILLO + "  [3]" + RESET + " 💰 Ver Total del Carrito");
            System.out.println(AMARILLO + "  [4]" + RESET + " ✅ Pagar (Checkout)");
            System.out.println(AMARILLO + "  [5]" + RESET + " 🚪 Salir");
            System.out.println(separador());
            System.out.print(CIAN + "  Seleccione una opción: " + RESET);

            String option = scanner.nextLine().trim();

            switch (option) {
                case "1" -> showCatalog();
                case "2" -> handleAddToCart();
                case "3" -> handleViewTotal();
                case "4" -> handleCheckout();
                case "5" -> {
                    running = false;
                    marketService.clearCart();
                }
                default -> System.out.println(error("Opción inválida. Seleccione del 1 al 5."));
            }
        }
    }

    private void showCatalog() {
        List<Product> catalog = marketService.getCatalog();

        System.out.println();
        System.out.println(titulo("  📦 CATÁLOGO DE PRODUCTOS"));
        System.out.println(separador());

        // Header de tabla
        System.out.printf(NEGRITA + BLANCO + "  %-5s %-28s %-12s %-18s %-10s" + RESET + "%n",
                "ID", "NOMBRE", "PRECIO", "CATEGORÍA", "TIPO");
        System.out.println(DIM + "  " + "─".repeat(75) + RESET);

        for (Product product : catalog) {
            String tipo;
            String tipoColor;
            if (product instanceof IPhysicalProduct) {
                tipo = "Físico";
                tipoColor = VERDE;
            } else if (product instanceof IDigitalProduct) {
                tipo = "Digital";
                tipoColor = MAGENTA;
            } else {
                tipo = "N/A";
                tipoColor = DIM;
            }

            System.out.printf("  " + AMARILLO + "%-5d" + RESET
                            + " %-28s "
                            + VERDE + "$%-11.2f" + RESET
                            + " " + CIAN + "%-18s" + RESET
                            + " " + tipoColor + "%-10s" + RESET + "%n",
                    product.getId(),
                    product.getName(),
                    product.getPrice(),
                    product.getCategory(),
                    tipo);
        }

        System.out.println(DIM + "  " + "─".repeat(75) + RESET);
        System.out.println(info("Total de productos disponibles: " + catalog.size()));
        System.out.println();
    }

    private void handleAddToCart() {
        System.out.print(CIAN + "  Ingrese el ID del producto: " + RESET);
        String input = scanner.nextLine().trim();

        try {
            int productId = Integer.parseInt(input);
            marketService.addToCart(productId);
            System.out.println(exito("Producto agregado al carrito exitosamente."));
        } catch (NumberFormatException e) {
            System.out.println(error("ID inválido. Debe ser un número entero."));
        } catch (IllegalArgumentException e) {
            System.out.println(error(e.getMessage()));
        }
    }

    private void handleViewTotal() {
        if (marketService instanceof MarketService ms) {
            if (ms.getCurrentCart().isEmpty()) {
                System.out.println(advertencia("El carrito está vacío."));
                return;
            }

            double subtotal = marketService.getCartTotal();
            double commission = ms.getCommission(currentUser);
            double total = subtotal + commission;
            String commissionName = ms.getCommissionName(currentUser.getType());

            System.out.println();
            System.out.println(titulo("  💰 RESUMEN DEL CARRITO"));
            System.out.println(separador());

            // Mostrar items del carrito
            List<Product> items = ms.getCurrentCart().getItems();
            System.out.printf(NEGRITA + BLANCO + "  %-5s %-30s %-12s" + RESET + "%n",
                    "ID", "PRODUCTO", "PRECIO");
            System.out.println(DIM + "  " + "─".repeat(50) + RESET);

            for (Product item : items) {
                System.out.printf("  " + AMARILLO + "%-5d" + RESET + " %-30s " + VERDE + "$%-11.2f" + RESET + "%n",
                        item.getId(), item.getName(), item.getPrice());
            }

            System.out.println(DIM + "  " + "─".repeat(50) + RESET);
            System.out.printf(BLANCO + "  %-36s" + RESET + VERDE + "$%-11.2f" + RESET + "%n",
                    "Subtotal:", subtotal);
            System.out.printf(BLANCO + "  %-36s" + RESET + AMARILLO + "$%-11.2f" + RESET + "%n",
                    "Comisión (" + commissionName + "):", commission);
            System.out.println(DIM + "  " + "─".repeat(50) + RESET);
            System.out.printf(NEGRITA + BLANCO + "  %-36s" + RESET + NEGRITA + VERDE + "$%-11.2f" + RESET + "%n",
                    "TOTAL A PAGAR:", total);
            System.out.println();
        }
    }

    private void handleCheckout() {
        try {
            // Mostrar resumen antes de confirmar
            if (marketService instanceof MarketService ms) {
                if (ms.getCurrentCart().isEmpty()) {
                    System.out.println(advertencia("El carrito está vacío. Agregue productos primero."));
                    return;
                }

                double subtotal = marketService.getCartTotal();
                double commission = ms.getCommission(currentUser);
                double total = subtotal + commission;

                System.out.println();
                System.out.println(titulo("  ✅ CONFIRMACIÓN DE PAGO"));
                System.out.println(separador());
                System.out.println(info("Productos en el carrito: " + ms.getCurrentCart().size()));
                System.out.println(info("Total a pagar: " + NEGRITA + VERDE + String.format("$%.2f", total) + RESET));
                System.out.print(AMARILLO + "  ¿Confirmar compra? (s/n): " + RESET);

                String confirm = scanner.nextLine().trim().toLowerCase();
                if (!confirm.equals("s") && !confirm.equals("si") && !confirm.equals("sí")) {
                    System.out.println(advertencia("Compra cancelada."));
                    return;
                }
            }

            marketService.checkout(currentUser);

            System.out.println();
            System.out.println(separadorDoble());
            System.out.println(NEGRITA + AZUL + "║" + RESET
                    + NEGRITA + VERDE + "         ¡COMPRA REALIZADA EXITOSAMENTE!                       " + RESET
                    + NEGRITA + AZUL + "║" + RESET);
            System.out.println(separadorCierre());
            System.out.println(exito("La venta ha sido registrada en el sistema."));
            System.out.println(info("Su carrito ha sido vaciado."));
            System.out.println();

        } catch (IllegalStateException e) {
            System.out.println(error(e.getMessage()));
        }
    }
}
