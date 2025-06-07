# Stock Screener Android App by Melvin Tang

## How To Set Up?
## Setup Instructions

1. Download ZIP file & Unzip
2. Open in Android Studio Meerkat Feature Drop
3. Build and run the application

## Features

- **Stock Screening**: Browse stock listings with real-time price information with filtering & search bar
- **Watchlist**: Save and manage favorite stocks (placeholder)
- **Dark/Light Mode**: Toggle between themes with persistent user preference

## App Structure

### Architecture
- **Pattern**: Fragment-based architecture with bottom navigation
- **Navigation**: 3-tab structure (Screener, Watchlist, Settings)
- **Data**: JSON-based stock data with Gson parsing
- **Theme**: System-wide dark/light mode support

### Key Components

```
MainActivity
├── ScreenerFragment (displays stock list from JSON with filtering feature)
├── WatchlistFragment (placeholder for saved stocks)
└── SettingsFragment (dark/light mode toggle)
```

## Trade-offs & Design Decisions

### Current Approach
**Pros:**
- Simple, maintainable structure
- Fast development and easy debugging

**Limitations:**
- Static JSON data (no real-time updates)
- Basic error handling
- No network connectivity

### Future Improvements

**Short-term:**
- Real-time stock data integration (Alpha Vantage, Yahoo Finance API)
- Enhanced error handling and loading states
- Search and filter functionality
- Pull-to-refresh implementation

**Medium-term:**
- MVVM architecture with ViewModels
- Notification system for price alerts

**Long-term:**
- Stock charts and detailed analytics
- Portfolio tracking features
- Advanced screening filters
- Real-time push notifications

## Screenshots
<img src="https://github.com/user-attachments/assets/5c629e27-a0ed-4e12-8724-c7a3f57a627b" width="400" />
<img src="https://github.com/user-attachments/assets/b542aeb5-1d81-4b9a-93b6-66a9a4747edf" width="400" />
<img src="https://github.com/user-attachments/assets/12762bc6-cfb5-479f-b6ac-9ed7ad03ca5e" width="400" />
<img src="https://github.com/user-attachments/assets/51605a9b-891b-46ef-ba77-711f17c9ab81" width="400" />




