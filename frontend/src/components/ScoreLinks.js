import React from 'react';

const ScoreLinks = () => (
    <div className="score-links">
        {[...Array(10)].map((_, i) => (
            <a key={i} href={`/anime-range?averageScoreGreater=${100 - i * 10}&averageScoreLesser=${90 - i * 10}`}>
                Scores: {100 - i * 10}-{90 - i * 10}
            </a>
        ))}
    </div>
);

export default ScoreLinks;
