# Client Contracts Management System

## Project Overview
This is a **JavaFX desktop application** to manage client contracts, connected to an **Oracle Database**.  

**Features:**
- Add new contracts for clients.
- Automatically generate installment schedules based on payment type:
  - Annual
  - Half-Annual
  - Quarterly
  - Monthly
- View contracts and navigate (First, Previous, Next, Last).
- Delete contracts safely with integrity constraints.
- Uses **triggers** and **sequences** for automation.

---

## Database Design

### Tables

**CLIENTS**
| Column     | Type | Notes       |
|------------|------|------------|
| CLIENT_ID  | INT  | Primary Key |

**CONTRACTS**
| Column                 | Type | Notes                          |
|------------------------|------|--------------------------------|
| CONTRACT_ID            | INT  | Primary Key (Sequence)        |
| CLIENT_ID              | INT  | Foreign Key → CLIENTS         |
| CONTRACT_STARTDATE      | DATE |                                |
| CONTRACT_ENDDATE        | DATE |                                |
| CONTRACT_TOTAL_FEES     | NUMBER |                               |
| CONTRACT_DEPOSIT_FEES   | NUMBER |                               |
| CONTRACT_PAYMENT_TYPE   | VARCHAR2 | ANNUAL, HALF_ANNUAL, QUARTER, MONTHLY |

**INSTALLMENTS_PAID**
| Column              | Type | Notes                          |
|------------------- |------|--------------------------------|
| INSTALLMENT_ID      | INT  | Primary Key (Sequence)        |
| CONTRACT_ID         | INT  | Foreign Key → CONTRACTS       |
| INSTALLMENT_DATE    | DATE |                                |
| INSTALLMENT_AMOUNT  | NUMBER |                               |

---

### Sequences

- `SEQ_CONTRACT_ID` → Generates unique Contract IDs.  
- `SEQ_INSTALLMENTS_PAID` → Generates unique Installment IDs.

---

### Trigger: `TRG_INS_CONTRACT`

- Fires **AFTER INSERT** on `CONTRACTS`.  
- Calculates installment schedule automatically based on:
  - Start & End Dates
  - Payment Type
  - Total Fees minus Deposit
- Inserts records into `INSTALLMENTS_PAID` table.

---

## UI Overview

### Main Panel (`ClientController.fxml`)
- Displays contracts with details:
  - Contract ID
  - Client ID
  - Start/End Dates
  - Total Fees
  - Deposit
  - Payment Type
- Buttons:
  - New → Opens form to add a new contract
  - Delete → Deletes selected contract
  - Navigation: First, Previous, Next, Last

**Example Layout:**  
<img width="620" height="600" alt="image" src="https://github.com/user-attachments/assets/8cb0bbe7-fb91-483e-8391-e3a66dcc01fa" />

---

### Add Contract Panel (`NewClient.fxml`)
- Fields:
  - ComboBox → Select Client
  - ComboBox → Select Payment Type
  - DatePicker → Start/End Date
  - TextField → Total Fees
  - TextField → Deposit Fees
- Buttons:
  - Commit → Saves contract & generates installments

**Example Layout:**  
<img width="695" height="640" alt="image" src="https://github.com/user-attachments/assets/8566ab2f-4110-48ef-8bee-07f8d383ae51" />

---

## How It Works

### Adding a Contract
1. Open "New Contract" panel.
2. Fill the details: Client, Payment Type, Dates, Fees.
3. Click **Commit** → Contract is inserted, trigger generates installments automatically.

### Viewing Contracts
- Navigate using First, Previous, Next, Last buttons.
- Contract details displayed in labels.

### Deleting Contracts
- Only possible if no installments exist for the contract.
- Deleting triggers **ORA-02292** if installments exist (foreign key enforcement).

---

## Setup Instructions

### Database
1. Create tables: `CLIENTS`, `CONTRACTS`, `INSTALLMENTS_PAID`.
2. Create sequences: `SEQ_CONTRACT_ID`, `SEQ_INSTALLMENTS_PAID`.
3. Create trigger `TRG_INS_CONTRACT`.

### Java Project
1. JDK 17+ (or compatible with JavaFX 25)
2. Add **JavaFX SDK** to project libraries.
3. Add Oracle JDBC driver (ojdbc8.jar).

### Running the Application
1. Open `ClientController` as main scene.
2. Click **New** to add contract.
3. Verify contract added & installments generated.

---

## Notes
- Rollback button cancels transaction if input is wrong.
- Trigger & sequences handle IDs automatically.
- Avoid manual inserts into `INSTALLMENTS_PAID`.
- Deleting contracts with existing installments will fail due to foreign key constraints.

---

## Future Improvements
- Search/filter contracts by client or date.
- Update contract functionality.
- Display installments in UI table.
- Add input validation for numeric fields, dates, and payment selection.
