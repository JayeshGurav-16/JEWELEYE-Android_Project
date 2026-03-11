# 💎 JEWELEYE – AR Jewellery Try-On Android Application

JEWELEYE is an **Android-based Augmented Reality (AR) jewellery try-on application** that enhances the online jewellery shopping experience. The application allows users to **virtually try on jewellery in real time** using face and neck tracking technology.

By combining **Augmented Reality with e-commerce features**, JEWELEYE helps users visualize jewellery products before purchasing, improving decision-making and increasing user engagement.

---

# 📱 Features

### 🔹 Real-Time AR Jewellery Try-On

* Uses **face and neck tracking** to overlay jewellery items on the user's live camera feed.
* Enables users to preview how jewellery looks before buying.

### 🔹 Interactive Product Catalogue

* Displays a list of available jewellery items with images and descriptions.
* Allows users to browse products easily.

### 🔹 3D Product Visualization

* Integrates **interactive 3D rendering** to simulate realistic jewellery placement.
* Improves the overall user experience.

### 🔹 Wishlist Functionality

* Users can save favourite jewellery items to their wishlist.
* Allows quick access to preferred products.

### 🔹 Shopping Cart

* Users can add products to a cart for potential purchase.
* Displays selected items and pricing.

### 🔹 Price Display

* Each product includes price information for easy comparison.

---

# 🏗️ System Architecture

The application follows a **client-server architecture**:

**Android Client**

* Handles UI, AR camera interaction, and user interactions.
* Processes face/neck tracking and overlays jewellery models.

**Firebase Backend**

* Stores product data, images, wishlist items, and user data.
* Provides real-time database synchronization.

---

# 🛠️ Tech Stack

| Technology                 | Purpose                              |
| -------------------------- | ------------------------------------ |
| **Java**                   | Core Android application development |
| **XML**                    | UI layout design                     |
| **Firebase**               | Backend services and database        |
| **Android Studio**         | Development environment              |
| **Augmented Reality (AR)** | Virtual jewellery try-on             |
| **3D Rendering**           | Display of jewellery models          |

---

# 📂 Project Structure

```
JEWELEYE
│
├── app
│   ├── java/com/jewelEye
│   │   ├── activities
│   │   ├── adapters
│   │   ├── models
│   │   ├── firebase
│   │   └── ar
│   │
│   ├── res
│   │   ├── layout
│   │   ├── drawable
│   │   ├── values
│   │   └── menu
│
├── assets
│   └── 3d_models
│
└── Firebase configuration files
```

---

# ⚙️ Installation

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/JEWELEYE.git
```

### 2️⃣ Open in Android Studio

* Open **Android Studio**
* Click **Open an Existing Project**
* Select the cloned repository folder

### 3️⃣ Setup Firebase

1. Create a Firebase project
2. Add your Android app in Firebase
3. Download the **google-services.json** file
4. Place it inside the **app/** directory

### 4️⃣ Build and Run

* Connect an **Android device** or start an **Android Emulator**
* Click **Run ▶** in Android Studio

---

# 🚀 Future Improvements

* Add **AI-based jewellery recommendations**
* Implement **online payment integration**
* Support **multiple face angles for better tracking**
* Improve **3D model realism**
* Add **user authentication and order tracking**

---

# 🎯 Use Cases

* Online jewellery stores
* Virtual jewellery showrooms
* Fashion and accessories preview apps
* E-commerce AR applications

---

# 👨‍💻 Author

Developed by **Jayesh Gurav**

