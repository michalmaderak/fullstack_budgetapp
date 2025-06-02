// src/pages/AddTransactionPage/AddTransactionPage.tsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Transaction, transactionsApi } from '../../api/transactionsApi';
import ErrorMessage from '../../components/ErrorMessage/ErrorMessage';
import CustomDialog from '../../components/CustomDialog/CustomDialog';
import styles from './AddTransactionPage.module.scss';

const AddTransactionPage: React.FC = () => {
  const navigate = useNavigate();
  const [amount, setAmount] = useState<number>(0);
  const [type, setType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE');
  const [tags, setTags] = useState<string>('');
  const [notes, setNotes] = useState<string>('');
  const [errors, setErrors] = useState<{ [key: string]: string }>({});
  const [showDialog, setShowDialog] = useState<boolean>(false);
  const [dialogMessage, setDialogMessage] = useState<string>('');

  const validate = (): boolean => {
    const newErrors: { [key: string]: string } = {};

    if (amount <= 0) {
      newErrors.amount = 'Kwota musi być większa od zera.';
    }

    if (!['INCOME', 'EXPENSE'].includes(type)) {
      newErrors.type = 'Wybierz poprawny typ transakcji.';
    }

    // Dodaj inne walidacje w razie potrzeby

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!validate()) return;

    const tagsArray = tags.split(',').map(tag => tag.trim()).filter(tag => tag);

    const newTransaction: Transaction = {
      amount,
      type,
      tags: tagsArray,
      notes,
    };

    try {
      await transactionsApi.create(newTransaction);
      alert('Transakcja dodana pomyślnie!');
      navigate('/transactions');
    } catch (error: unknown) {
      setDialogMessage('Błąd dodawania transakcji.' + error);
      setShowDialog(true);
    }
  };

  const closeDialog = () => {
    setShowDialog(false);
  };

  return (
    <div className={styles.container}>
      <h2>Dodaj Transakcję</h2>
      <form onSubmit={handleSubmit} className={styles.form}>
        <div className={styles.formGroup}>
          <label>Kwota:</label>
          <input 
            type="number" 
            value={amount} 
            onChange={(e) => setAmount(parseFloat(e.target.value))} 
            required 
            className={styles.input}
          />
          {errors.amount && <ErrorMessage message={errors.amount} />}
        </div>
        <div className={styles.formGroup}>
          <label>Typ:</label>
          <select 
            value={type} 
            onChange={(e) => setType(e.target.value as 'INCOME' | 'EXPENSE')} 
            className={styles.input}
          >
            <option value="INCOME">Przychód</option>
            <option value="EXPENSE">Wydatek</option>
          </select>
          {errors.type && <ErrorMessage message={errors.type} />}
        </div>
        <div className={styles.formGroup}>
          <label>Tagi (oddzielone przecinkami):</label>
          <input 
            type="text" 
            value={tags} 
            onChange={(e) => setTags(e.target.value)} 
            placeholder="np. jedzenie, transport"
            className={styles.input}
          />
        </div>
        <div className={styles.formGroup}>
          <label>Notatki:</label>
          <textarea 
            value={notes} 
            onChange={(e) => setNotes(e.target.value)} 
            placeholder="Dodatkowe informacje"
            className={styles.textarea}
          />
        </div>
        {errors.general && <ErrorMessage message={errors.general} />}
        <button type="submit" className={styles.button}>Dodaj</button>
      </form>

      {showDialog && (
        <CustomDialog 
          title="Błąd"
          message={dialogMessage}
          onClose={closeDialog}
        />
      )}
    </div>
  );
};

export default AddTransactionPage;
