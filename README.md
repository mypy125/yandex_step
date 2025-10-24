# Ecommerce Project

## Project Description

**Ecommerce-multi_vendor** This project is an e-commerce platform with functionality divided into three main roles: user (customers), sellers, and administrators. The backend handles all the business logic, data processing, storage, user authentication, order and product management, and integrations with external services like email notifications and payment gateways.

- **Location:** Yerevan
- **Launch Date:** 10/2025
- **Developer:** Gagikovich Gor Mkhitaryan
- **Project Diagram:** [Ecommerce_Project_Structure](https://miro.com/app/board/uXjVJ12s_mg=/?share_link_id=379785323839)

**App Services Dependencies**
Authentication Service
dependencies:
  - User Service (для проверки учетных данных)
  - Redis (для хранения токенов)

Order Service
dependencies:
  - Product Service (проверка наличия товаров)
  - User Service (информация о пользователе)
  - Payment Service (обработка платежей)
  - Coupon Service (применение скидок)

Payment Service
dependencies:
  - Order Service (обновление статуса заказа)
  - Notification Service (уведомления о платежах)

