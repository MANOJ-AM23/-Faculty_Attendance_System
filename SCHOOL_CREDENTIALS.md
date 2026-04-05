# 🎓 School Attendance System - Login Credentials

## ✅ System Status
- **Application**: Running ✅
- **URL**: http://localhost:8080
- **Port**: 8080
- **Database**: H2 (In-memory)
- **Status**: Ready to use

---

## 📊 Admin Account

| Field | Value |
|-------|-------|
| **Username** | `admin` |
| **Password** | `admin123` |
| **Role** | Administrator |
| **Department** | Administration |

### Admin Portal Features:
- View all staff attendance
- Manage staff records (edit/delete)
- Generate attendance reports
- **Approve/Reject leave requests**
- View pending leave applications
- Track statistics (staff present/absent today)

---

## 👨‍🏫 Staff Accounts (10 Staff Members)

### Staff Member 1
| Field | Value |
|-------|-------|
| **Username** | `mr_sharma` |
| **Password** | `password123` |
| **Name** | Mr. Sharma |
| **Subject** | Mathematics |

### Staff Member 2
| Field | Value |
|-------|-------|
| **Username** | `ms_patel` |
| **Password** | `password123` |
| **Name** | Ms. Patel |
| **Subject** | English |

### Staff Member 3  
| Field | Value |
|-------|-------|
| **Username** | `dr_singh` |
| **Password** | `password123` |
| **Name** | Dr. Singh |
| **Subject** | Science |

### Staff Member 4
| Field | Value |
|-------|-------|
| **Username** | `mr_gupta` |
| **Password** | `password123` |
| **Name** | Mr. Gupta |
| **Subject** | Social Studies |

### Staff Member 5
| Field | Value |
|-------|-------|
| **Username** | `ms_verma` |
| **Password** | `password123` |
| **Name** | Ms. Verma |
| **Subject** | Hindi |

### Staff Member 6
| Field | Value |
|-------|-------|
| **Username** | `mr_khan` |
| **Password** | `password123` |
| **Name** | Mr. Khan |
| **Subject** | Physical Education |

### Staff Member 7
| Field | Value |
|-------|-------|
| **Username** | `ms_desai` |
| **Password** | `password123` |
| **Name** | Ms. Desai |
| **Subject** | Computer Science |

### Staff Member 8
| Field | Value |
|-------|-------|
| **Username** | `mr_mishra` |
| **Password** | `password123` |
| **Name** | Mr. Mishra |
| **Subject** | Arts |

### Staff Member 9
| Field | Value |
|-------|-------|
| **Username** | `ms_chopra` |
| **Password** | `password123` |
| **Name** | Ms. Chopra |
| **Subject** | Biology |

### Staff Member 10
| Field | Value |
|-------|-------|
| **Username** | `mr_rao` |
| **Password** | `password123` |
| **Name** | Mr. Rao |
| **Subject** | Chemistry |

---

## 🔐 Quick Reference

### All Staff Passwords
```
All staff members use the same password: password123
```

### Admin Credentials
```
Username: admin
Password: admin123
```

---

## 📋 Staff Portal Features

Each staff member can:
- ✅ View daily attendance
- ✅ Check monthly attendance report with charts
- ✅ Apply for leave
- ✅ Track leave request status
- ✅ View approval/rejection status
- ✅ See attendance percentage

---

## 🎯 How to Test the System

### Step 1: Login as Admin
1. Go to http://localhost:8080
2. Enter Username: `admin`
3. Enter Password: `admin123`
4. Click **Sign In**

### Step 2: View Admin Dashboard
- See Total Staff count (should show 11: 1 admin + 10 staff)
- View staff present/absent today
- Check pending leave requests
- Manage staff members

### Step 3: Approve/Reject Leaves
1. Go to "Leave Requests" section
2. View all pending requests
3. Click **✅ Approve** or **❌ Reject**
4. Confirm your action

### Step 4: Login as Staff Member
1. Logout from admin
2. Login with any staff username (e.g., `mr_sharma`)
3. Use password: `password123`

### Step 5: Apply for Leave
1. Go to "Apply Leave" section
2. Select From and To dates
3. Enter reason
4. Click **Apply**
5. See status change to PENDING

### Step 6: Check Leave in Admin
1. Logout as staff
2. Login as admin again
3. Go to "Leave Requests"
4. Approve or Reject the leave
5. Logout and login as staff to see status update

---

## 🎨 User Interface Updates

The system has been converted to school context:
- ✅ "Faculty" → "Staff"
- ✅ "University" → "School"  
- ✅ "Department" → Subject (displayed)
- ✅ Professional color-coded status badges
- ✅ Real-time auto-refresh (30 seconds for staff, 10 seconds for admin stats)
- ✅ Font Awesome icons throughout
- ✅ Responsive design for mobile/tablet/desktop

---

## 📊 System Data

### Default Data Created on First Run
- 1 Admin account
- 10 Staff members in various subjects
- H2 in-memory database (resets on app restart)

### Database
- **Type**: H2 In-Memory
- **Auto-initialization**: Yes (data created on startup)
- **Data Persistence**: Session only (resets on restart)

---

## 🚀 Access Points

### Login Page
- **URL**: http://localhost:8080
- **Method**: Browser

### Staff Dashboard  
- **URL**: http://localhost:8080/staff-dashboard (after login)
- **Features**: Attendance, Leave, Reports

### Admin Dashboard
- **URL**: http://localhost:8080/admin-dashboard (after login)
- **Features**: Management, Approval, Reporting

---

## ✨ Features Overview

| Feature | Staff | Admin |
|---------|:-----:|:-----:|
| View Attendance | ✅ | ✅ |
| Apply Leave | ✅ | ✅ |
| Track Leave Status | ✅ | ✅ |
| Monthly Report | ✅ | ✅ |
| Attendance Chart | ✅ | ✅ |
| Manage Staff | ❌ | ✅ |
| Approve Leave | ❌ | ✅ |
| Reject Leave | ❌ | ✅ |
| View Statistics | ✅ | ✅ |
| Generate Reports | ❌ | ✅ |

---

## 🆘 Troubleshooting

### "Connection Refused" Error
- **Solution**: Ensure port 8080 is not blocked. Check if application is running.

### Can't Login
- **Solution**: Double-check username and password (case-sensitive)
- **Default Admin**: `admin` / `admin123`

### Data Not Persisting
- **Note**: Using H2 in-memory. Data resets when app restarts.
- **Solution**: Switch to MySQL for persistent data (see documentation)

### Leave Not Appearing in Admin
- **Solution**: 
  1. Logout as staff
  2. Login as admin
  3. Navigate to "Leave Requests" section
  4. Refresh the page

---

## 📝 Important Notes

1. **Session Timeout**: 30 minutes (auto-logout)
2. **Default Attendance Status**: ABSENT (must mark PRESENT to change)
3. **Leave Applications**: Automatically visible to admin in Leave Requests
4. **Password Security**: Passwords are encrypted using BCrypt
5. **Multiple Logins**: Each user can be logged in from different browsers simultaneously

---

## 🔄 System Workflow

```
LEAVE WORKFLOW:
┌─────────────────────────────────────┐
│ Staff Applies for Leave             │
│ - Select dates and reason           │
│ - Submit application                │
│ - Status: PENDING                   │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│ Admin Reviews Pending Leave         │
│ - See all pending requests          │
│ - Click Approve or Reject           │
│ - Confirm action                    │
└──────────────┬──────────────────────┘
               │
        ┌──────┴──────┐
        ▼             ▼
    APPROVED      REJECTED
        │             │
        ▼             ▼
    Staff sees    Staff sees
    Approved      Rejected
```

---

## 📱 Device Support

Works on:
- ✅ Desktop (Chrome, Firefox, Safari, Edge)
- ✅ Tablet (iPad, Android tablets)
- ✅ Mobile (iPhone, Android phones)

---

**Date Generated**: February 14, 2026  
**System Version**: 1.0.0  
**Status**: Production Ready ✅

---

## 📞 Quick Start

1. **Open Browser**: Go to http://localhost:8080
2. **Login**:
   - **Admin**: admin / admin123
   - **Staff**: mr_sharma / password123 (or any other staff)
3. **Explore**: Dashboard, Attendance, Leave Management
4. **Test Workflow**: Submit leave → Approve as admin → See update

**Ready to use! 🎓**
