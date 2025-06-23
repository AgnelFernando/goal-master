## **Overview**

**Goal Master** is a Kotlin-based Android application designed to help users set, manage, and track progress on personal or professional goals.

---

## **How the Goal Master App Works**

### **Goal Creation**

Users can create a goal by providing:

* **Name**
* **Due Date**
* **Total Units** (e.g., number of pages in a book, milestones in a project)
* **Definition of Done** (criteria for when the goal is considered complete)
* **Description** *(optional)*

Each goal tracks progress using a horizontal progress bar. Units completed through task completion update the bar:

* **Grey** indicates units added via tasks.



* **Green** indicates completed units.

A goal can be in one of three states:

* `ACTIVE`
* `COMPLETED` (automatically set when `completedUnits` equals `totalUnits`)




* `ARCHIVED` (manually set by the user)

---

### **Task Management**

Users can create multiple tasks under a goal. Each task includes:

* **Name**
* **Unit Size** (must be ≤ total units of the goal)
* **Definition of Done**
* **Duration** (between 10 minutes to 3 hours)
* **Description** *(optional)*

Each task has one of four states:

* `CREATED`
* `UNPLANNED`
* `PLANNED`
* `DONE`

Once a task is added to a plan, it cannot be deleted.

---

### **Planning**

Users can create a plan by specifying a:

* **Start Date**
* **End Date**

Within this range, users assign tasks to specific dates using the plan view.

#### **Plan Page**

* Displays each day in the selected range.
* Shows **total hours planned per day** (computed from task durations).
* “0s” indicates no task is scheduled for that day.
* Tasks are shown in the order they were added.

To add a task:

1. Tap the **plus icon** on a date.
2. Select a task from the available list (excluding archived goals).
3. Assign the task to the chosen date.

Once all tasks are scheduled, users can **lock** the plan. Locked plans prevent any further edits to tasks or schedule. If needed, users can **delete the plan**, which moves all `PLANNED` tasks back to the `UNPLANNED` state.

---

### **Task Completion**

On the plan page, tapping a task expands it to reveal action icons:

* ✅ **Complete**: Marks the task as `DONE` and updates the goal’s `completedUnits`.




* 👁️ **View**
* 📅 **Add to Calendar**
* ❌ **Delete** (only visible if the task is not yet planned)

---

### **Archiving and Filtering**

Users can switch between viewing:

* **Active Goals**
* **Archived Goals**
* **Completed Goals**

Archived goals do not appear in the task selection screen while planning.













