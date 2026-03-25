package com.ar.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Random;

import org.springframework.stereotype.Service;

import com.ar.entity.Orders;
import java.awt.Color;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class PdfService {

    public ByteArrayInputStream generatePdf(Orders order) throws Exception {

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // ===== Fonts =====
        Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(58, 28, 113));
        Font subTitleFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.DARK_GRAY);
        Font sectionFont = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(239, 62, 91));
        Font textFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.BLACK);
        Font boldFont = new Font(Font.HELVETICA, 11, Font.BOLD, Color.BLACK);
        Font totalFont = new Font(Font.HELVETICA, 13, Font.BOLD, new Color(58, 28, 113));

        // ===== Dynamic values =====
        double subtotal = order.getPrice() * order.getQuantity();
        double cgst = subtotal * 0.025;   // 2.5%
        double sgst = subtotal * 0.025;   // 2.5%
        double hst = subtotal * 0.02;     // 1%
        double grandTotal = subtotal + cgst + sgst + hst;

        String paymentMethod = "Cash";
        String bikeNumber = generateBikeNumber();

        String customerLocation = "Jaipur, Rajasthan";
        String restaurantLocation = order.getRestaurantName() + ", Main City Branch";

        // ===== Title =====
        Paragraph title = new Paragraph("FOOD Wala INVOICE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph line2 = new Paragraph("Delicious food delivered with care", subTitleFont);
        line2.setAlignment(Element.ALIGN_CENTER);
        line2.setSpacingAfter(18f);
        document.add(line2);

        // ===== Customer + Delivery Info =====
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setSpacingAfter(15f);
        infoTable.setWidths(new float[]{1, 1});

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.BOX);
        leftCell.setPadding(10f);
        leftCell.setBorderColor(new Color(220, 220, 220));
        leftCell.addElement(new Paragraph("Customer Details", sectionFont));
        leftCell.addElement(new Paragraph("Name: " + order.getCustomerName(), textFont));
        leftCell.addElement(new Paragraph("Location: " + customerLocation, textFont));
        leftCell.addElement(new Paragraph("Payment Method: " + paymentMethod, textFont));

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.BOX);
        rightCell.setPadding(10f);
        rightCell.setBorderColor(new Color(220, 220, 220));
        rightCell.addElement(new Paragraph("Delivery Details", sectionFont));
        rightCell.addElement(new Paragraph("Restaurant: " + order.getRestaurantName(), textFont));
        rightCell.addElement(new Paragraph("Restaurant Location: " + restaurantLocation, textFont));
        rightCell.addElement(new Paragraph("Assigned Bike No: " + bikeNumber, textFont));

        infoTable.addCell(leftCell);
        infoTable.addCell(rightCell);

        document.add(infoTable);

        // ===== Food Item Table =====
        Paragraph foodHeading = new Paragraph("Order Summary", sectionFont);
        foodHeading.setSpacingAfter(10f);
        document.add(foodHeading);

        PdfPTable foodTable = new PdfPTable(5);
        foodTable.setWidthPercentage(100);
        foodTable.setWidths(new float[]{3, 2, 2, 2, 2});
        foodTable.setSpacingAfter(15f);

        addHeaderCell(foodTable, "Food Name");
        addHeaderCell(foodTable, "Category");
        addHeaderCell(foodTable, "Price");
        addHeaderCell(foodTable, "Quantity");
        addHeaderCell(foodTable, "Subtotal");

        addBodyCell(foodTable, order.getFoodName());
        addBodyCell(foodTable, order.getCategory());
        addBodyCell(foodTable, "Rs. " + order.getPrice());
        addBodyCell(foodTable, String.valueOf(order.getQuantity()));
        addBodyCell(foodTable, "Rs. " + subtotal);

        document.add(foodTable);

        // ===== Tax Summary =====
        Paragraph taxHeading = new Paragraph("Tax & Payment Summary", sectionFont);
        taxHeading.setSpacingAfter(10f);
        document.add(taxHeading);

        PdfPTable totalTable = new PdfPTable(2);
        totalTable.setWidthPercentage(45);
        totalTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTable.setSpacingAfter(18f);

        addSummaryRow(totalTable, "Subtotal", "Rs. " + format(subtotal), boldFont, textFont);
        addSummaryRow(totalTable, "CGST (2.5%)", "Rs. " + format(cgst), boldFont, textFont);
        addSummaryRow(totalTable, "SGST (2.5%)", "Rs. " + format(sgst), boldFont, textFont);
        addSummaryRow(totalTable, "Delivery charges", "Rs. " + format(hst), boldFont, textFont);
        addSummaryRow(totalTable, "Grand Total", "Rs. " + format(grandTotal), totalFont, totalFont);

        document.add(totalTable);

        // ===== Delivery Status =====
        Paragraph deliveryHeading = new Paragraph("Delivery Status", sectionFont);
        deliveryHeading.setSpacingAfter(8f);
        document.add(deliveryHeading);

        Paragraph deliveryText = new Paragraph(
                "Your food location has been confirmed. The order will be delivered from "
                        + restaurantLocation + " to " + customerLocation
                        + ". Assigned vehicle number: " + bikeNumber + ".",
                textFont);
        deliveryText.setSpacingAfter(18f);
        document.add(deliveryText);

        // ===== Footer =====
        Paragraph thanks = new Paragraph("Thank you for ordering with Food Hut!", totalFont);
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingAfter(8f);
        document.add(thanks);

        Paragraph footer = new Paragraph(
                "Payment Mode: " + paymentMethod + " | Safe Delivery | Enjoy your meal",
                subTitleFont);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addHeaderCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 11, Font.BOLD, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setBackgroundColor(new Color(58, 28, 113));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addBodyCell(PdfPTable table, String text) {
        Font font = new Font(Font.HELVETICA, 10, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Paragraph(text, font));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(8f);
        table.addCell(cell);
    }

    private void addSummaryRow(PdfPTable table, String label, String value, Font leftFont, Font rightFont) {
        PdfPCell left = new PdfPCell(new Paragraph(label, leftFont));
        left.setBorder(Rectangle.BOX);
        left.setPadding(8f);
        left.setBorderColor(new Color(220, 220, 220));

        PdfPCell right = new PdfPCell(new Paragraph(value, rightFont));
        right.setBorder(Rectangle.BOX);
        right.setPadding(8f);
        right.setHorizontalAlignment(Element.ALIGN_RIGHT);
        right.setBorderColor(new Color(220, 220, 220));

        table.addCell(left);
        table.addCell(right);
    }

    private String generateBikeNumber() {
        String[] states = {"RJ", "DL", "UP", "HR", "MP"};
        Random random = new Random();

        String state = states[random.nextInt(states.length)];
        int district = 10 + random.nextInt(90);
        char series1 = (char) ('A' + random.nextInt(26));
        char series2 = (char) ('A' + random.nextInt(26));
        int number = 1000 + random.nextInt(9000);

        return state + district + " " + series1 + series2 + " " + number;
    }

    private String format(double value) {
        return String.format("%.2f", value);
    }
}