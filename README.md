# NHS Jobs Search Automation Suite - BhumikaVaghasiya-NHSBSA

## 🔍 Project Objective

This project automates the **Search functionality** of the NHS Jobs website (https://www.jobs.nhs.uk/candidate/search) using **User-Centric BDD** with Cucumber, Selenium, and Java 21.  

It fulfills the following test criteria from the sprint story:

- Search using "What" and "Where"
- View job results
- Sort the results by **Date Posted (Newest)**

---

## 📄 User Story

**As a** jobseeker on the NHS Jobs website  
**I want to** search for a job with my preferences  
**So that** I can get recently posted job results  

### ✅ Acceptance Criteria

- **Given** I am a jobseeker on NHS Jobs website  
- **When** I enter my preferences in the Search functionality  
- **Then** I should get a list of jobs which match my preferences  
- **And** sort my search results with the newest Date Posted  

---

## ✅ Test Coverage

| Feature | Status |
|--------|--------|
| Enter job title and location | ✅ |
| Click Search and wait for results | ✅ |
| Validate search results contain expected text | ✅ |
| Sort results by "Date Posted (newest)" | ✅ |
| Cross-browser: Chrome & Firefox | ✅ |

---

## 💻 Tech Stack

| Tool            | Purpose                            |
|-----------------|------------------------------------|
| Java 21         | Programming language               |
| Maven           | Dependency and build management    |
| Selenium        | UI automation                      |
| Cucumber (Gherkin) | BDD framework                 |
| WebDriverManager | Driver management (no local drivers) |
| Log4j           | Logging output                     |
| JUnit           | Test runner                        |

---

## ⚙️ How to Run the Tests

### Prerequisites

- **Java 21+** installed
- **Maven 3+** installed
- Internet connection

### Clone and Run the Project

```bash
# Clone this repository
git clone https://github.com/Bhumika/Bhumika-NHSBSA.git
cd Bhumika-NHSBSA

# Run in Chrome
mvn clean test -Dbrowser=chrome

# Run in Firefox
mvn clean test -Dbrowser=firefox
