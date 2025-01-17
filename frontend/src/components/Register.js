import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../styles/styles.css';

const Register = () => {
    const [username, setUsername] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const navigate = useNavigate();

    const handleRegister = async (e) => {
    e.preventDefault();

    if (password !== confirmPassword) {
        setErrorMessage("Passwords do not match.");
        return;
    }

    setErrorMessage(''); // Clear any previous error messages

    try {
        const response = await fetch('http://localhost:8080/register', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                username,
                email,
                password,
            }),
        });

        if (response.ok) {
            navigate('/login'); // Redirect to login page after successful registration
        } else if (response.status === 409) {
            setErrorMessage('Username is already taken. Please choose another one.');
        } else {
            const errorData = await response.text();
            setErrorMessage(errorData || 'Registration failed. Please try again.');
        }
    } catch (error) {
        console.error('Registration error:', error);
        setErrorMessage('An error occurred. Please try again.');
    }
};


    return (
        <div className="container">
            <h1 className="mt-5">Registration</h1>

            <form onSubmit={handleRegister}>
                <div className="form-group">
                    <label htmlFor="username">Username</label>
                    <input
                        type="text"
                        id="username"
                        className="form-control"
                        placeholder="Enter Your Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="email">Email</label>
                    <input
                        type="text"
                        id="email"
                        className="form-control"
                        placeholder="Enter Your Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="password">Password</label>
                    <input
                        type="password"
                        id="password"
                        className="form-control"
                        placeholder="Enter Your Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="confirmPassword">Confirm Password</label>
                    <input
                        type="password"
                        id="confirmPassword"
                        className="form-control"
                        placeholder="Confirm Your Password"
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                    />
                </div>
                {errorMessage && <p className="text-danger">{errorMessage}</p>}
                <button type="submit" className="btn btn-primary">Register</button>
            </form>

            <div className="mt-3">
                <p>
                    Already have an account?{' '}
                    <a href='/login'>Log in here</a>
                </p>
            </div>
        </div>
    );
};

export default Register;
