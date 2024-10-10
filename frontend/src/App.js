import React from 'react';
import './styles/styles.css';
import Header from './components/Header';
import MainContent from './components/MainContent';
import ScoreLinks from './components/ScoreLinks';
import Footer from './components/Footer';
import AnimeModal from './components/AnimeModal'

const App = () => {
    const [username, setUsername] = React.useState("User"); // For demo purposes
    const [modalData, setModalData] = React.useState(null); // Manage modal state

    return (
        <div className="App">
            <Header username={username} />
            <MainContent />
            <ScoreLinks />
            <Footer />
            {modalData && (
                <AnimeModal
                    title={modalData.title}
                    description={modalData.description}
                    onClose={() => setModalData(null)}
                />
            )}
        </div>
    );
};

export default App;
