import React, { useState, useEffect } from 'react';
import { getUnreadNotifications, getUnreadExpiryNotifications, markNotificationAsRead } from './NotificationService';
import './Notification.css'; // For styling

const Notifications = () => {
    const [notifications, setNotifications] = useState([]); // For low stock notifications
    const [expiryNotifications, setExpiryNotifications] = useState([]); // For expiry notifications
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [marking, setMarking] = useState(null);
    const [successMessage, setSuccessMessage] = useState(null);

    // Fetch both unread notifications when the component mounts
    useEffect(() => {
        const fetchNotifications = async () => {
            setLoading(true);
            setError(null);
            try {
                // Fetch both low stock and expiry notifications
                const data = await getUnreadNotifications(); // Fetch low stock notifications
                const expiryData = await getUnreadExpiryNotifications(); // Fetch expiry notifications

                setNotifications(data);
                setExpiryNotifications(expiryData);
            } catch (err) {
                setError(err.message || 'Error fetching notifications. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchNotifications();
    }, []);

    // Handle marking notification as read
    const handleMarkAsRead = async (notificationId) => {
        setMarking(notificationId);
        try {
            await markNotificationAsRead(notificationId);
            setNotifications(prevNotifications =>
                prevNotifications.filter(notification => notification.id !== notificationId)
            );
            setExpiryNotifications(prevNotifications =>
                prevNotifications.filter(notification => notification.id !== notificationId)
            );
            setSuccessMessage('Notification marked as read!');
        } catch (err) {
            setError(err.message || 'Error marking notification as read.');
        } finally {
            setMarking(null);
            setTimeout(() => setSuccessMessage(null), 3000);
        }
    };

    return (
        <div className="notification-container">
            <h3>Notifications</h3>

            {error && <p className="error">{error}</p>}
            {successMessage && <p className="success">{successMessage}</p>}

            {loading ? (
                <p>Loading notifications...</p> // Replace this with a spinner if desired
            ) : (
                <>
                    {/* Low Stock Notifications */}
                    {notifications.length > 0 && (
                        <div>
                            <h4>Low Stock Notifications</h4>
                            <ul>
                                {notifications.map(notification => (
                                    <li key={notification.id} className="notification-item">
                                        <div 
                                            className={`notification-message ${marking === notification.id ? 'marking' : ''}`} 
                                            style={{ color: marking === notification.id ? 'green' : 'red' }}
                                        >
                                            {notification.message}
                                        </div>
                                        <button 
                                            onClick={() => handleMarkAsRead(notification.id)}
                                            disabled={marking === notification.id} // Disable button while marking
                                        >
                                            {marking === notification.id ? 'Marking...' : 'Mark as Read'}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    {/* Expiry Notifications */}
                    {expiryNotifications.length > 0 && (
                        <div>
                            <h4>Expiry Notifications</h4>
                            <ul>
                                {expiryNotifications.map(notification => (
                                    <li key={notification.id} className="notification-item">
                                        <div 
                                            className={`notification-message ${marking === notification.id ? 'marking' : ''}`} 
                                            style={{ color: marking === notification.id ? 'green' : 'red' }}
                                        >
                                            {notification.message}
                                        </div>
                                        <button 
                                            onClick={() => handleMarkAsRead(notification.id)}
                                            disabled={marking === notification.id} // Disable button while marking
                                        >
                                            {marking === notification.id ? 'Marking...' : 'Mark as Read'}
                                        </button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}

                    {/* If no notifications */}
                    {notifications.length === 0 && expiryNotifications.length === 0 && (
                        <p>No unread notifications</p>
                    )}
                </>
            )}
        </div>
    );
};

export default Notifications;