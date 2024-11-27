import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './itemselection.css';
import SearchBox from '../../../../components/search-box/SearchBox';
import Pagination from '../../../../components/pagination/Pagination';
import { API } from '../../../../env';
import userVerification from '../../../../utils/userVerification';
import Loading from '../../../../components/loading/Loading';

const ItemSelection = ({ onSelectionChange, provider }) => {
    const [query, setQuery] = useState('');
    const [page, setPage] = useState(1);
    const pageSize = 5;

    const [isLoading, setIsLoading] = useState(true);
    const [paginator, setPaginator] = useState({});
    const navigate = useNavigate();
    const [articles, setArticles] = useState([]);

    useEffect(() => {
        if (!provider) return;

        // Permission validation
        if (!userVerification().isAuthenticated) {
            localStorage.clear();
            navigate('/login');
            return;
        }

        // Query paginated data
        const queryParams = new URLSearchParams({
            ...(query && { searchCriteria: query }),
            providerId: provider.providerId,
            page,
            pageSize,
        }).toString();

        // Use `http` in case there's an SSL error on localhost (replace with `https` for production)
        const url = `${API}/article?${queryParams}`;

        const fetchData = async () => {
            setIsLoading(true);
            try {
                const response = await fetch(url);
                if (!response.ok) throw new Error(`API error: ${response.statusText}`);
                const data = await response.json();

                if (!data.articles) throw new Error("Articles data is missing");

                // Set articles and paginator
                setArticles(data.articles); // Set articles directly here
                setPaginator(data); // Update paginator for pagination control
            } catch (error) {
                console.error("Error fetching articles:", error.message);
            } finally {
                setIsLoading(false);
            }
        };

        fetchData();
    }, [query, page, provider, navigate]);

    const handleSearch = (query) => setQuery(query);

    const handlePage = (page) => setPage(page);

    const handleCheckboxChange = (article, isChecked) => {
        if (isChecked) {
            article.quantity = 1;
            const updatedArticles = [...articles, article];
            setArticles(updatedArticles);
            onSelectionChange(updatedArticles);
        } else {
            const updatedArticles = articles.filter(a => a.articleId !== article.articleId);
            setArticles(updatedArticles);
            onSelectionChange(updatedArticles);
        }
    };

    const handleQuantityChange = (article, newQuantity) => {
        newQuantity = newQuantity.length === 0 ? 1 : parseInt(newQuantity, 10);
        const updatedArticles = articles.map(
            a => a.articleId === article.articleId ? { ...a, quantity: newQuantity } : a
        );
        setArticles(updatedArticles);
        onSelectionChange(updatedArticles);
    };

    const calculateTotal = () => {
        return articles.reduce((total, article) => total + article.quantity * article.purchasePrice, 0);
    };

    return (
        <div className="item-selection-container">
            <div className="options">
                <SearchBox onSearch={handleSearch} disabled={isLoading} />
            </div>

            {!isLoading ? (
                <div className="table-container">
                    <table className="table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>NAME</th>
                                <th>BRAND</th>
                                <th>CATEGORIES</th>
                                <th>STOCK</th>
                                <th>PURCHASE PRICE</th>
                                <th>SALE PRICE</th>
                                <th>PROVIDER</th>
                                <th>SELECT</th>
                            </tr>
                        </thead>
                        <tbody>
                            {paginator.articles && paginator.articles.length > 0 ? (
                                paginator.articles.map(article => (
                                    <tr key={article.articleId}>
                                        <td>{article.articleId}</td>
                                        <td>{article.name}</td>
                                        <td>{article.brand}</td>
                                        <td>{article.category?.name || 'N/A'}</td>
                                        <td>{article.stock}</td>
                                        <td>{article.purchasePrice ? article.purchasePrice.toLocaleString('es-NP', { style: 'currency', currency: 'NPR' }) : 'N/A'}</td>
                                        <td>{article.salePrice ? article.salePrice.toLocaleString('es-NP', { style: 'currency', currency: 'NPR' }) : 'N/A'}</td>
                                        <td>{article.provider?.name || 'N/A'}</td>
                                        <td>
                                            <label className="checkbox-container">
                                                <input
                                                    type="checkbox"
                                                    checked={!!articles.find(a => a.articleId === article.articleId)}
                                                    onChange={(event) => handleCheckboxChange(article, event.target.checked)}
                                                />
                                                <span className="checkmark"></span>
                                            </label>
                                        </td>
                                    </tr>
                                ))
                            ) : (
                                <tr>
                                    <td colSpan="9">No result found.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                    <Pagination paginator={paginator} onChangePage={handlePage} />
                </div>
            ) : (
                <Loading />
            )}

            {articles.length > 0 && (
                <div className="purchaseSummary">
                    <div className="top-purchase">
                        <hr />
                        <label>PURCHASE SUMMARY</label>
                    </div>
                    <div className="table-container">
                        <table className="table">
                            <thead>
                                <tr>
                                    <th>ID</th>
                                    <th>NAME</th>
                                    <th>BRAND</th>
                                    <th>STOCK</th>
                                    <th>QUANTITY</th>
                                    <th>PRICE</th>
                                    <th>SUBTOTAL</th>
                                    <th>REMOVE</th>
                                </tr>
                            </thead>
                            <tbody>
                                {articles.map(article => (
                                    <tr key={article.articleId}>
                                        <td>{article.articleId}</td>
                                        <td>{article.name}</td>
                                        <td>{article.brand}</td>
                                        <td>{article.stock}</td>
                                        <td>
                                            <input
                                                className="input"
                                                type="number"
                                                min="1"
                                                value={article.quantity}
                                                onChange={(event) => handleQuantityChange(article, event.target.value)}
                                                required
                                            />
                                        </td>
                                        <td>{article.purchasePrice.toLocaleString('es-NP', { style: 'currency', currency: 'NPR' })}</td>
                                        <td>{(article.purchasePrice * article.quantity).toLocaleString('es-NP', { style: 'currency', currency: 'NPR' })}</td>
                                    </tr>
                                ))}
                                <tr>
                                    <td colSpan="5"></td>
                                    <td className="total">TOTAL</td>
                                    <td className="total">{calculateTotal().toLocaleString('es-NP', { style: 'currency', currency: 'NPR' })}</td>
                                    <td></td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            )}
        </div>
    );
};

export default ItemSelection;
