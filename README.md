# 🌍 Expense Manager - Trabajo Práctico Integrador 2024

> Proyecto desarrollado como parte del TPI 2024 para las asignaturas **Laboratorio de Computación IV**, **Programación IV** y **Metodología de Sistemas** de la carrera *Tecnicatura Universitaria en Programación* de la *Universidad Tecnológica Nacional - Facultad Regional Córdoba*.

---

## 📋 Descripción del Proyecto
Tiempo de Desarrollo:

Fecha de inicio: 15/09/2024 Fecha Fin: 19/11/2024


**Expense Manager** es un microservicio orientado a la gestión eficiente de gastos en un entorno compartido, como un consorcio de propietarios, barrios cerrados o countries. Desarrollada en **Java** con **Spring Boot** para el backend, **MySQL** para el manejo de datos, y **Docker** para la contenerización, esta herramienta permite:

- **Registrar y clasificar** diversos tipos de gastos.
- **Gestionar consultas** de gastos comunes e individuales.
- **Auditar cambios** para asegurar trazabilidad y transparencia.

*(Añadí aquí detalles específicos de las funcionalidades del proyecto)*

---

## 🛠️ Tecnologías Utilizadas

| Tecnología        | Descripción                                      |
|-------------------|---------------------------------------------------|
| **Java**          | Lenguaje de programación principal               |
| **Spring Boot**   | Framework para simplificar el desarrollo en Java |
| **MySQL**         | Base de datos relacional                         |
| **Docker**        | Contenerización para despliegue y portabilidad   |

---

## 🚀 Requisitos Previos

Para correr este proyecto, asegúrate de tener las siguientes herramientas instaladas:

- **Docker** y **Docker Compose**
- **Java JDK 17**
- **Maven** para gestionar dependencias
- **Git** (opcional, para clonar el repositorio)

---

## 📦 Instrucciones de Configuración y Ejecución

### 1. Clonar el Repositorio

Clona el repositorio en tu máquina local:

```bash
git clone <URL_DEL_REPOSITORIO>
cd expense-manager
```
Package el proyecto:

```bash
mvn clean package -DskipTests 
```
Construir el Docker:

```bash
docker compose build
```

Levantar el Docker:

```bash
docker compose up
```
## Endpoints

### Controlador `BillExpenseController`

- **`@RequestMapping("/billexpenses")`**
- **`@PostMapping("/generate")`**
    - **Descripción**: Genera las expensas de acuerdo al periodo especificado.
    - **Parámetros**:
        - `periodDto` (cuerpo de la solicitud): Información sobre el periodo de tiempo para generar las expensas.
    - **Respuesta**: Retorna un objeto `BillExpenseDto` con los datos de las expensas generadas.
    - - **Respuesta**:
    ```json
    {
      "id": 123,
      "owners": [
        {
          "id": 1,
          "fieldSize": 100,
          "fines": [
            {
              "id": 1,
              "plotId": 1,
              "description": "Multa por retraso",
              "amount": 50.00
            }
          ],
          "expensesCommon": [
            {
              "id": 1,
              "description": "Mantenimiento general",
              "amount": 1000.00
            }
          ],
          "expensesExtraordinary": [
            {
              "id": 2,
              "description": "Reparación del techo",
              "amount": 2500.00
            }
          ],
          "expensesIndividual": [
            {
              "id": 3,
              "description": "Consumo de agua",
              "amount": 100.00
            }
          ],
          "notesOfCredit": [
            {
              "id": 4,
              "description": "Crédito por pago anticipado",
              "amount": 50.00
            }
          ]
        }
      ]
    }
    ```

### Controlador `ExpenseCategoryController`

- **`@RequestMapping("/categories")`**
- **`@PostMapping("/postCategory")`**
    - **Descripción**: Crea una nueva categoría de gasto.
    - **Parámetros**:
        - `description` (cadena): Descripción de la categoría.
    - **Respuesta**:
      ```json
      {
        "id": 1,
        "description": "Mantenimiento",
        "lastUpdatedDatetime": "2023-11-05 12:34:56",
        "state": "Activo"
      }
      ```

- **`@PutMapping("/putById")`**
    - **Descripción**: Actualiza una categoría de gasto existente.
    - **Parámetros**:
        - `id` (entero): ID de la categoría.
        - `description` (cadena, opcional): Nueva descripción.
        - `enabled` (booleano, opcional): Estado de la categoría.
    - **Respuesta**:
      ```json
      {
        "id": 1,
        "description": "Reparaciones",
        "lastUpdatedDatetime": "2023-11-06 09:15:30",
        "state": "Inactivo"
      }
      ```

- **`@GetMapping("/all")`**
    - **Descripción**: Recupera todas las categorías disponibles.
    - **Respuesta**:
      ```json
      [
        {
          "id": 1,
          "description": "Mantenimiento",
          "lastUpdatedDatetime": "2023-11-05 12:34:56",
          "state": "Activo"
        },
        {
          "id": 2,
          "description": "Reparaciones",
          "lastUpdatedDatetime": "2023-11-06 09:15:30",
          "state": "Inactivo"
        }
      ]
      ```

### Controlador `ExpenseController`

- **`@RequestMapping("/expenses")`**
- **`@PostMapping`**
    - **Descripción**: Crea un nuevo gasto con detalles y un archivo opcional.
    - **Parámetros**:
        - `expense` (objeto): Detalles del gasto.
        - `file` (archivo, opcional): Archivo adjunto.
    - **Respuesta**:
      ```json
      {
        "description": "Compra de suministros",
        "providerId": 123,
        "expenseDate": "2023-11-01",
        "fileId": "12345678-abcd-1234-efgh-1234567890ab",
        "invoiceNumber": "INV-2023-001",
        "expenseType": "COMUN",
        "dtoCategory": {
          "id": 1,
          "description": "Mantenimiento",
          "lastUpdatedDatetime": "2023-11-05 12:34:56",
          "state": "Activo"
        },
        "dtoDistributionList": [
          {
            "ownerId": 1,
            "proportion": 0.5
          },
          {
            "ownerId": 2,
            "proportion": 0.5
          }
        ],
        "dtoInstallmentList": [
          {
            "installmentNumber": 1,
            "paymentDate": "2023-12-01"
          },
          {
            "installmentNumber": 2,
            "paymentDate": "2024-01-01"
          }
        ]
      }
      ```

- **`@PutMapping`**
    - **Descripción**: Edita un gasto existente.
    - **Parámetros**:
        - `expense` (objeto): Detalles actualizados del gasto.
        - `file` (archivo, opcional): Archivo adjunto actualizado.
    - **Respuesta**:
      ```json
      {
        "description": "Compra de suministros (actualizado)",
        "providerId": 123,
        "expenseDate": "2023-11-01",
        "fileId": "12345678-abcd-1234-efgh-1234567890ab",
        "invoiceNumber": "INV-2023-001",
        "expenseType": "COMUN",
        "dtoCategory": {
          "id": 1,
          "description": "Mantenimiento",
          "lastUpdatedDatetime": "2023-11-05 12:34:56",
          "state": "Activo"
        },
        "dtoDistributionList": [
          {
            "ownerId": 1,
            "proportion": 0.5
          },
          {
            "ownerId": 2,
            "proportion": 0.5
          }
        ],
        "dtoInstallmentList": [
          {
            "installmentNumber": 1,
            "paymentDate": "2023-12-01"
          },
          {
            "installmentNumber": 2,
            "paymentDate": "2024-01-01"
          }
        ]
      }
      ```

- **`@DeleteMapping`**
    - **Descripción**: Elimina lógicamente un gasto por su ID.
    - **Parámetros**:
        - `id` (entero): ID del gasto.
    - **Respuesta**:
      ```json
      {
        "expense": "Compra de suministros",
        "descriptionResponse": "Gasto eliminado exitosamente",
        "httpStatus": "OK"
      }
      ```

- **`@GetMapping("/getById")`**
    - **Descripción**: Recupera un gasto específico por su ID.
    - **Parámetros**:
        - `expenseId` (entero): ID del gasto.
    - **Respuesta**:
      ```json
      {
        "id": 1,
        "description": "Compra de suministros",
        "category": "Mantenimiento",
        "categoryId": 1,
        "provider": "Proveedor ABC",
        "providerId": 123,
        "amount": 1000.00,
        "expenseType": "COMUN",
        "expenseDate": "2023-11-01",
        "fileId": "12345678-abcd-1234-efgh-1234567890ab",
        "distributionList": [
          {
            "ownerId": 1,
            "ownerFullName": "John Doe",
            "amount": 500.00,
            "proportion": 0.5
          },
          {
            "ownerId": 2,
            "ownerFullName": "Jane Smith",
            "amount": 500.00,
            "proportion": 0.5
          }
        ],
        "installmentList": [
          {
            "paymentDate": "2023-12-01",
            "installmentNumber": 1
          },
          {
            "paymentDate": "2024-01-01",
            "installmentNumber": 2
          }
        ],
        "invoiceNumber": "INV-2023-001"
      }
      ```

- **`@GetMapping("/getByFilters")`**
    - **Descripción**: Filtra los gastos según el tipo, categoría, proveedor y rango de fechas.
    - **Parámetros**:
        - `expenseType` (cadena, opcional): Tipo de gasto.
        - `category` (cadena, opcional): Categoría del gasto.
        - `provider` (cadena, opcional): Proveedor del gasto.
        - `dateFrom` (fecha): Fecha de inicio.
        - `dateTo` (fecha): Fecha de fin.
    - **Respuesta**:
      ```json
      [
        {
          "id": 1,
          "description": "Compra de suministros",
          "category": "Mantenimiento",
          "categoryId": 1,
          "provider": "Proveedor ABC",
          "providerId": 123,
          "amount": 1000.00,
          "expenseType": "COMUN",
          "expenseDate": "2023-11-01",
          "fileId": "12345678-abcd-1234-efgh-1234567890ab",
          "distributionList": [
            {
              "ownerId": 1,
              "ownerFullName": "John Doe",
              "amount": 500.00,
              "proportion": 0.5
            },
            {
              "ownerId": 2,
              "ownerFullName": "Jane Smith",
              "amount": 500.00,
              "proportion": 0.5
            }
          ],
          "installmentList": [
            {
              "paymentDate": "2023-12-01",
              "installmentNumber": 1
            },
            {
              "paymentDate": "2024-01-01",
              "installmentNumber": 2
            }
          ],
          "invoiceNumber": "INV-2023-001"
        }
      ]
      ```

### Controlador `ExpenseDistributionController`

- **`@RequestMapping("/api/expenses/distributions")`**
- **`@GetMapping("/getAllByOwnerId")`**
    - **Descripción**: Recupera todos los gastos asociados a un propietario, dentro de un rango de fechas.
    - **Parámetros**:
        - `id` (entero): ID del propietario.
        - `startDate` (fecha): Fecha de inicio.
        - `endDate` (fecha): Fecha de fin.
    - **Respuesta**:
      ```json
      [
        {
          "id": 1,
          "expenseId": 1,
          "description": "Compra de suministros",
          "providerId": 123,
          "providerDescription": "Proveedor ABC",
          "expenseDate": "2023-11-01",
          "fileId": "12345678-abcd-1234-efgh-1234567890ab",
          "invoiceNumber": "INV-2023-001",
          "expenseType": "COMUN",
          "category": {
            "id": 1,
            "description": "Mantenimiento"
          },
          "amount": 1000.00,
          "proportion": 0.5,
          "installments": 2,
          "enabled": true
        }
      ]
      ```

### Controlador `ExpenseReportChartController`

- **`@RequestMapping("/reportchart")`**
- **`@GetMapping("/yearmonth")`**
    - **Descripción**: Obtiene los meses y años en los que se han registrado gastos.
    - **Respuesta**:
      ```json
      [
        {
          "year": 2023,
          "month": 11,
          "amount": 5000.00,
          "expenseType": "COMUN",
          "providerId": 123,
          "categoryId": 1
        },
        {
          "year": 2023,
          "month": 12,
          "amount": 2500.00,
          "expenseType": "EXTRAORDINARIO",
          "providerId": 456,
          "categoryId": 2
        }
      ]
      ```

- **`@GetMapping("/categoriesperiod")`**
    - **Descripción**: Recupera los gastos por categorías en un periodo de tiempo.
    - **Parámetros**:
        - `startDate` (fecha): Fecha de inicio.
        - `endDate` (fecha): Fecha de fin.
    - **Respuesta**:
      ```json
      [
        {
          "category": "Mantenimiento",
          "amount": 3000.00
        },
        {
          "category": "Reparaciones",
          "amount": 2500.00
        }
      ]
      ```

- **`@GetMapping("/expenseByTypeAndCategory")`**
    - **Descripción**: Obtiene los gastos agrupados por tipo y categoría en un periodo de tiempo.
    - **Parámetros**:
        - `startDate` (fecha): Fecha de inicio.
        - `endDate` (fecha): Fecha de fin.
    - **Respuesta**:
      ```json
      [
        {
          "expenseType": "COMUN",
          "categoryId": 1,
          "description": "Mantenimiento",
          "amount": 3000.00,
          "providerId": 123
        },
        {
          "expenseType": "EXTRAORDINARIO",
          "categoryId": 2,
          "description": "Reparaciones",
          "amount": 2500.00,
          "providerId": 456
        }
      ]
      ```

- **`@GetMapping("/lastBillRecord")`**
    - **Descripción**: Recupera el último registro de facturación.
    - **Respuesta**:
      ```json
      {
        "id": 1,
        "bills": [
          {
            "expenseType": "COMUN",
            "categoryId": 1,
            "description": "Mantenimiento",
            "amount": 3000.00,
            "providerId": 123
          },
          {
            "expenseType": "EXTRAORDINARIO",
            "categoryId": 2,
            "description": "Reparaciones",
            "amount": 2500.00,
            "providerId": 456
          }
        ],
        "fineAmount": 500.00,
        "pendingAmount": 1000.00
      }
      ```
 
