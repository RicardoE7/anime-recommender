import React, { useState } from 'react';
import '../styles/styles.css';  // Restore original import

const ProfileModal = ({ isOpen, onClose, username, email, onSave }) => {
	const [currentUsername, setCurrentUsername] = useState(username);
	const [newUsername, setNewUsername] = useState('');
	const [newEmail, setNewEmail] = useState(email);
	const [currentPassword, setCurrentPassword] = useState('');
	const [newPassword, setNewPassword] = useState('');
	const [error, setError] = useState('');

	const handleSave = async () => {
		if (!currentPassword) {
			setError('Current password is required to make changes.');
			return;
		}

		setError(''); // Clear any previous error messages

		try {
			const response = await fetch('http://localhost:8080/update-profile', {
				method: 'POST',
				headers: {
					'Content-Type': 'application/json',
				},
				body: JSON.stringify({
					username: currentUsername,
					newUsername,
					email: newEmail,
					newPassword,
					currentPassword,
				}),
			});

			if (response.ok) {
				console.log('Profile updated successfully.');
				setCurrentUsername(newUsername);  // Update the currentUsername with the newUsername after a successful update
				onSave(newUsername || currentUsername, newEmail); // Pass updated username to parent
				onClose(); // Close the modal on success
			}
			else {
				const errorData = await response.json();
				setError(errorData.message || 'Failed to update profile. Please try again.');
			}
		} catch (error) {
			console.error('Profile update error:', error);
			setError('An error occurred. Please try again.');
		}
	};



	if (!isOpen) return null;

	return (
		<div className="modal-overlay">
			<div className="modal-content">
				<h2>Edit Profile</h2>
				{error && <p className="error">{error}</p>}
				<div className="form-group">
					<label>Current Username:</label>
					<input
						type="text"
						value={currentUsername}
						onChange={(e) => setCurrentUsername(e.target.value)}
						readOnly // Prevent editing of current username
					/>
				</div>
				<div className="form-group">
					<label>New Username:</label>
					<input
						type="text"
						value={newUsername}
						onChange={(e) => setNewUsername(e.target.value)}
						placeholder="Enter new username"
					/>
				</div>
				<div className="form-group">
					<label>Email:</label>
					<input
						type="email"
						value={newEmail}
						onChange={(e) => setNewEmail(e.target.value)}
					/>
				</div>
				<div className="form-group">
					<label>Current Password:</label>
					<input
						type="password"
						value={currentPassword}
						onChange={(e) => setCurrentPassword(e.target.value)}
						required
					/>
				</div>
				<div className="form-group">
					<label>New Password:</label>
					<input
						type="password"
						value={newPassword}
						onChange={(e) => setNewPassword(e.target.value)}
					/>
				</div>
				<div className="modal-actions">
					<button onClick={handleSave}>Save</button>
					{error && <p className="text-danger">{error}</p>} {/* Error message displayed here */}
					<button onClick={onClose}>Cancel</button>
				</div>
			</div>
		</div>
	);
};

export default ProfileModal;

