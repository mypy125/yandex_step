# Ecommerce Project

## Project Description

**Ecommerce-multi_vendor** This project is an e-commerce platform with functionality divided into three main roles: user (customers), sellers, and administrators. The backend handles all the business logic, data processing, storage, user authentication, order and product management, and integrations with external services like email notifications and payment gateways.

- **Location:** Yerevan
- **Launch Date:** 10/2025
- **Developer:** Gagikovich Gor Mkhitaryan
- **Project Diagram:** [Ecommerce_Project_Structure](https://miro.com/app/board/uXjVJ12s_mg=/?share_link_id=379785323839)

**API-Gateway**

Gateway responsibilities:
 - Bearer token availability check
 - CORS protection
 - Rate limiting
 - Request logging
 - Load balancing

**App Services Dependencies**

Authentication Service
dependencies:
  - User Service (to verify credentials)
  - Redis (to saving tokens)

Order Service
dependencies:
  - Product Service (checking the availability of goods)
  - User Service (user information)
  - Payment Service (payment processing)
  - Coupon Service (application of discounts)

Payment Service
dependencies:
  - Order Service (order status update)
  - Notification Service (payment notifications)

